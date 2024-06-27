package com.easemob.app.service.impl;

import com.easemob.app.exception.ASDuplicateUniquePropertyExistsException;
import com.easemob.app.exception.ASNotFoundException;
import com.easemob.app.exception.ASPasswordErrorException;
import com.easemob.app.model.AppUser;
import com.easemob.app.model.AppUserInfo;
import com.easemob.app.model.ChatGptMessage;
import com.easemob.app.model.TokenInfo;
import com.easemob.app.service.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AppUserServiceImpl implements AppUserService, InitializingBean {

    @Value("${application.agora.chat.appkey}")
    private String appkey;

    @Value("${easemob.chat.robot.name}")
    private String chatRobotName;

    @Value("${easemob.chat.default.group.member.bella}")
    private String defaultChatGroupMemberBella;

    @Value("${easemob.chat.default.group.member.miles}")
    private String defaultChatGroupMemberMiles;

    @Value("${easemob.thread.pool.core.size}")
    private Integer coreSize;

    @Value("${easemob.thread.pool.max.size}")
    private Integer maxSize;

    @Value("${easemob.thread.pool.keepAlive.seconds}")
    private Integer keepAlive;

    @Value("${easemob.thread.pool.queue.capacity}")
    private Integer queueCapacity;

    @Value("${easemob.call.rest.api.time.interval.millisecond}")
    private Long callRestApiTimeIntervalMillisecond;

    private final static String USER = "user";
    private static final String ASSISTANT = "assistant";
    private static final String AI_CHAT_BOT_USER_PROMPT =
            "Hello, I'm the Agora AI chatbot. I can understand human language and generate content, serving as your intelligent assistant for both life and work.";
    private static final String AI_CHAT_BOT_GROUP_PROMPT =
            "Welcome to this group powered by Agora Chat! I am an AI companion and your assistant. Feel free to @AgoraChatAI to start a conversation with me within this group.";
    private static final String MILES_GROUP_MESSAGE_A = "Dinner party on Sunday？";
    private static final String BELLA_GROUP_MESSAGE_A = "I'm in，may be vegetarian?";
    private static final String MILES_GROUP_MESSAGE_B = "Err, I might need some ideas...";
    private static final String MILES_GROUP_MESSAGE_C = "@AI Chatbot Vegetarian recipe ideas";
    private static final String AI_CHAT_BOT_GROUP_ANSWER_MESSAGE =
            "Certainly! Here are some vegetarian recipe ideas for your dinner party on Sunday:\n"
                    + "1. Roasted Veggie Tacos: Include a variety of roasted vegetables like bell peppers, zucchini, and onions, and serve with your favorite toppings like avocado, salsa, and lime.\n"
                    + "2. Stuffed Bell Peppers: Fill bell peppers with a mixture of quinoa, black beans, corn, and spices, and bake until tender.\n"
                    + "3. Lentil Shepherd's Pie: A comforting dish made with lentils, vegetables, and topped with mashed potatoes.\n"
                    + "4. Caprese Salad Skewers: Skewer cherry tomatoes, fresh basil leaves, and mini mozzarella balls, then drizzle with balsamic glaze.\n"
                    + "5. Spinach and Feta Stuffed Mushrooms: Stuff mushrooms with a mixture of sautéed spinach, feta cheese, breadcrumbs, and herbs, then bake until golden brown.\n"
                    + "I hope these ideas inspire your vegetarian dinner party menu! Let me know if you need more suggestions.";
    @Autowired
    private TokenService tokenService;

    @Autowired
    private AssemblyService assemblyService;

    @Autowired
    private RestService restService;

    @Autowired
    private RedisService redisService;

    private ThreadPoolExecutor threadPool;

    @Override public void afterPropertiesSet() {
        threadPool = new ThreadPoolExecutor(
                coreSize,
                maxSize,
                keepAlive,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadFactoryBuilder().setNameFormat("chat-gpt").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void registerWithChatUser(AppUser appUser) {
        String chatUserName = appUser.getUserAccount().toLowerCase();
        String chatUserPassword = appUser.getUserPassword();

        if (this.assemblyService.checkIfUserAccountExistsDB(appkey, chatUserName)) {
            throw new ASDuplicateUniquePropertyExistsException(
                    "userAccount " + chatUserName + " already exists");
        } else {
            this.assemblyService.saveAppUserToDB(appkey, chatUserName, null,
                    chatUserPassword, chatUserName,
                    this.assemblyService.generateUniqueAgoraUid(appkey));

            if (!this.restService.checkIfChatUserNameExists(appkey, chatUserName)) {
                this.restService.registerChatUserName(appkey, chatUserName);
            }

            threadPool.execute(() -> {
                try {
                    createBusinessData(chatUserName);
                } catch (InterruptedException e) {
                    log.error("create business data error. chatUserName : {}", chatUserName, e);
                }
            });
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TokenInfo loginWithChatUser(AppUser appUser) {
        String userAccount = appUser.getUserAccount().toLowerCase();

        if (this.assemblyService.checkIfUserAccountExistsDB(appkey, userAccount)) {
            AppUserInfo userInfo =
                    this.assemblyService.getAppUserInfoFromDB(appkey, userAccount);
            if (userInfo.getUserPassword() == null) {
                this.assemblyService.updateAppUserToDB(appkey, userInfo.getId(),
                        userInfo.getUserAccount(), userInfo.getUserNickname(),
                        appUser.getUserPassword(), userInfo.getChatUserName(),
                        userInfo.getAgoraUid());
            } else {
                if (!appUser.getUserPassword().equals(userInfo.getUserPassword())) {
                    throw new ASPasswordErrorException("userAccount password error");
                }
            }

            if (!this.restService.checkIfChatUserNameExists(appkey, userAccount)) {
                this.restService.registerChatUserName(appkey, userAccount);
            }
        } else {
            throw new ASNotFoundException("userAccount " + userAccount + " does not exist");
        }

        return this.tokenService.getUserTokenWithAccount(appkey, userAccount);
    }

    /**
     * 用户注册成功后，创建业务数据，包括给用户加好友，给用户发送消息，创建群组，给群组发送消息，用于在客户端demo上展示
     *
     * @param chatUserName chatUserName
     */
    private void createBusinessData(String chatUserName) throws InterruptedException {
        this.restService.addContact(appkey, chatUserName, chatRobotName);
        this.restService.sendTextMessageToUser(appkey, chatRobotName, chatUserName,
                AI_CHAT_BOT_USER_PROMPT, null);

        String groupId = this.restService.createChatGroup(appkey, chatUserName);

        this.restService.sendTextMessageToGroup(appkey, chatRobotName, groupId,
                AI_CHAT_BOT_GROUP_PROMPT, null);
        Thread.sleep(callRestApiTimeIntervalMillisecond);

        this.restService.sendTextMessageToGroup(appkey, defaultChatGroupMemberMiles,
                groupId,
                assemblyContentMessage(defaultChatGroupMemberMiles, MILES_GROUP_MESSAGE_A), null);
        this.redisService.addGroupMessageToRedis(appkey, groupId,
                ChatGptMessage.builder().role(USER).content(MILES_GROUP_MESSAGE_A).build());
        Thread.sleep(callRestApiTimeIntervalMillisecond);

        this.restService.sendTextMessageToGroup(appkey, defaultChatGroupMemberBella,
                groupId,
                assemblyContentMessage(defaultChatGroupMemberBella, BELLA_GROUP_MESSAGE_A), null);
        this.redisService.addGroupMessageToRedis(appkey, groupId,
                ChatGptMessage.builder().role(USER).content(BELLA_GROUP_MESSAGE_A).build());
        Thread.sleep(callRestApiTimeIntervalMillisecond);

        this.restService.sendTextMessageToGroup(appkey, defaultChatGroupMemberMiles,
                groupId,
                assemblyContentMessage(defaultChatGroupMemberMiles, MILES_GROUP_MESSAGE_B), null);
        this.redisService.addGroupMessageToRedis(appkey, groupId,
                ChatGptMessage.builder().role(USER).content(MILES_GROUP_MESSAGE_B).build());
        Thread.sleep(callRestApiTimeIntervalMillisecond);

        this.restService.sendTextMessageToGroup(appkey, defaultChatGroupMemberMiles,
                groupId,
                assemblyContentMessage(defaultChatGroupMemberMiles, MILES_GROUP_MESSAGE_C), null);
        this.redisService.addGroupMessageToRedis(appkey, groupId,
                ChatGptMessage.builder().role(USER).content(MILES_GROUP_MESSAGE_C).build());
        Thread.sleep(callRestApiTimeIntervalMillisecond);

        this.restService.sendTextMessageToGroup(appkey, chatRobotName, groupId,
                AI_CHAT_BOT_GROUP_ANSWER_MESSAGE, null);
        this.redisService.addGroupMessageToRedis(appkey, groupId,
                ChatGptMessage.builder().role(ASSISTANT).content(AI_CHAT_BOT_GROUP_ANSWER_MESSAGE)
                        .build());
    }

    private String assemblyContentMessage(String username, String message) {
        return String.format("%s said:%s", username, message);
    }
}
