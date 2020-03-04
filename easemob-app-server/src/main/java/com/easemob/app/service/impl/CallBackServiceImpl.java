package com.easemob.app.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.app.model.ChatGptMessage;
import com.easemob.app.service.CallBackService;
import com.easemob.app.service.ChatGptService;
import com.easemob.app.service.RedisService;
import com.easemob.app.service.RestService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CallBackServiceImpl implements CallBackService, InitializingBean {

    private final static String CHAT_TYPE = "chat";
    private final static String GROUP_CHAT_TYPE = "groupchat";
    private final static String TXT_MESSAGE = "txt";
    private final static String TYPE = "type";
    private final static String MSG = "msg";
    private final static String BODIES = "bodies";
    private final static String EXT = "ext";
    private final static String EM_AT_LIST = "em_at_list";
    private final static String EASE_CHAT_UIKIT_USER_INFO = "ease_chat_uikit_user_info";
    private final static String AI_CHAT_BOT = "AI Chatbot";
    private final static String AT_AI_CHAT_BOT = "@AI Chatbot";
    private final static String NICKNAME = "nickname";
    private final static String USER = "user";
    private static final String ASSISTANT = "assistant";
    private static final String EXCEEDING_LIMIT_PROMPT = "Sorry, you have reached the question limit. Please wait 24 hours or switch accounts to continue.";
    private static final String REQUEST_TIMEOUT_PROMPT = "The request has exceeded the response time, please retry.";

    @Value("${application.agora.chat.appkey}")
    private String chatAppkey;

    @Value("${easemob.chat.robot.name}")
    private String chatRobotName;

    @Value("${easemob.thread.pool.core.size}")
    private Integer coreSize;

    @Value("${easemob.thread.pool.max.size}")
    private Integer maxSize;

    @Value("${easemob.thread.pool.keepAlive.seconds}")
    private Integer keepAlive;

    @Value("${easemob.thread.pool.queue.capacity}")
    private Integer queueCapacity;

    @Value("${send.message.to.chatgpt.count.limit.switch}")
    private Boolean countLimitSwitch;

    @Value("${send.chatgpt.answer.message.length.limit}")
    private int sendMessageLengthLimit;

    private ChatGptService chatGptService;

    private RestService restService;

    private RedisService redisService;

    private ThreadPoolExecutor threadPool;

    public CallBackServiceImpl(ChatGptService chatGptService, RestService restService,
            RedisService redisService) {
        this.chatGptService = chatGptService;
        this.restService = restService;
        this.redisService = redisService;
    }

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

    @Override
    public void receiveCallBackMessage(String appKey, String from, String to, String chatType,
            String groupId, JSONObject callBackPayload) {

        log.info(
                "receive call back message. appKey : {}, from : {}, to : {}, chatType : {}, groupId : {}, callBackPayload : {}",
                appKey, from, to, chatType, groupId, callBackPayload);

        if (appKey.equals(chatAppkey)) {
            switch (chatType) {
                case CHAT_TYPE:
                    handleChatMessage(appKey, from, to, callBackPayload);
                    break;
                case GROUP_CHAT_TYPE:
                    handleChatGroupMessage(appKey, from, groupId, callBackPayload);
                    break;
                default:
                    break;
            }
        }
    }

    private void handleChatMessage(String appKey, String from, String to, JSONObject callBackPayload) {
        if (to.equalsIgnoreCase(chatRobotName)) {
            JSONArray messageBodyList = callBackPayload.getJSONArray(BODIES);
            if (messageBodyList != null && messageBodyList.size() > 0) {
                JSONObject messageBody = messageBodyList.getJSONObject(0);
                if (messageBody != null) {
                    if (messageBody.getString(TYPE).equals(TXT_MESSAGE)) {
                        threadPool.execute(() -> {
                            if (countLimitSwitch) {
                                boolean result = redisService.checkIfSendMessageToChatGptLimit(appKey, from);
                                if (result) {
                                    restService.sendTextMessageToUser(chatAppkey, chatRobotName, from, EXCEEDING_LIMIT_PROMPT, getChatbotErrorExt());
                                    return;
                                }
                            }

                            String chatGptAnswer = null;
                            try {
                                chatGptAnswer = chatGptService.sendChatMessage(appKey, from, messageBody.getString(MSG));
                            } catch (Exception e) {
                                log.error("send chat message to chat gpt fail. error : {}", e.getMessage());
                            }

                            if (chatGptAnswer != null) {
                                restService.sendTextMessageToUser(chatAppkey, chatRobotName, from, chatGptAnswer, null);
                            } else {
                                restService.sendTextMessageToUser(chatAppkey, chatRobotName, from, REQUEST_TIMEOUT_PROMPT, getChatbotErrorExt());
                            }
                        });
                    }
                }
            }
        }
    }

    private void handleChatGroupMessage(String appKey, String from, String groupId, JSONObject callBackPayload) {
        JSONArray messageBodyList = callBackPayload.getJSONArray(BODIES);
        if (messageBodyList != null && messageBodyList.size() > 0) {
            JSONObject messageBody = messageBodyList.getJSONObject(0);
            if (messageBody != null) {
                if (messageBody.getString(TYPE).equals(TXT_MESSAGE)) {
                    JSONObject ext = callBackPayload.getJSONObject(EXT);
                    if (ext != null) {
                        String messageContentUsername;
                        JSONObject userInfo = ext.getJSONObject(EASE_CHAT_UIKIT_USER_INFO);
                        if (userInfo == null) {
                            messageContentUsername = from;
                        } else {
                            Object nickname = userInfo.get(NICKNAME);
                            if (nickname != null && StringUtils.isNotBlank(nickname.toString())) {
                                messageContentUsername = nickname.toString();
                            } else {
                                messageContentUsername = from;
                            }
                        }

                        JSONArray atList = ext.getJSONArray(EM_AT_LIST);
                        if (atList != null && atList.size() > 0) {
                            for (Object user : atList) {
                                if (user.toString().equalsIgnoreCase(chatRobotName)) {
                                    threadPool.execute(() -> {
                                        if (countLimitSwitch) {
                                            boolean result = redisService.checkIfSendMessageToChatGptLimit(appKey, from);
                                            if (result) {
                                                restService.sendTextMessageToGroupMember(chatAppkey, chatRobotName, groupId, from, EXCEEDING_LIMIT_PROMPT, getChatbotErrorExt());
                                                return;
                                            }
                                        }

                                        String chatGptAnswer = null;
                                        try {
                                            String messageContent = messageBody.getString(MSG);
                                            if (messageContent.contains(AT_AI_CHAT_BOT) || messageContent.contains(AT_AI_CHAT_BOT + " ")) {
                                                messageContent = messageContent.replace(AT_AI_CHAT_BOT, "");
                                            }
                                            chatGptAnswer = chatGptService.sendChatGroupMessage(appKey, messageContentUsername, groupId, messageContent);
                                        } catch (Exception e) {
                                            log.error("send chat group message to chat gpt fail. error : {}", e.getMessage());
                                        }

                                        chatGptAnswerHandle(appKey, groupId, chatGptAnswer);
                                    });
                                    break;
                                } else {
                                    redisService.addGroupMessageToRedis(appKey, groupId,
                                            ChatGptMessage.builder().role(USER)
                                                    .content(assemblyContentMessage(messageContentUsername, messageBody.getString(MSG))).build());
                                }
                            }
                        } else {
                            redisService.addGroupMessageToRedis(appKey, groupId,
                                    ChatGptMessage.builder().role(USER)
                                            .content(assemblyContentMessage(messageContentUsername, messageBody.getString(MSG))).build());
                        }
                    } else {
                        redisService.addGroupMessageToRedis(appKey, groupId,
                                ChatGptMessage.builder().role(USER)
                                        .content(assemblyContentMessage(from, messageBody.getString(MSG))).build());
                    }
                }
            }
        }
    }

    private void chatGptAnswerHandle(String appKey, String groupId, String chatGptAnswer) {
        if (chatGptAnswer != null) {
            int length = chatGptAnswer.length();
            if (chatGptAnswer.length() > sendMessageLengthLimit) {
                for (int i = 0; i < length; i += sendMessageLengthLimit) {
                    String messageSegmentation = chatGptAnswer.substring(i, Math.min(length, i + sendMessageLengthLimit));
                    restService.sendTextMessageToGroup(chatAppkey, chatRobotName, groupId, messageSegmentation, null);

                    redisService.addGroupMessageToRedis(appKey, groupId,
                            ChatGptMessage.builder().role(ASSISTANT).content(messageSegmentation)
                                    .build());
                }
            } else {
                restService.sendTextMessageToGroup(chatAppkey, chatRobotName, groupId, chatGptAnswer, null);

                redisService.addGroupMessageToRedis(appKey, groupId,
                        ChatGptMessage.builder().role(ASSISTANT).content(chatGptAnswer)
                                .build());
            }
        } else {
            restService.sendTextMessageToGroup(chatAppkey, chatRobotName, groupId, REQUEST_TIMEOUT_PROMPT, getChatbotErrorExt());
        }
    }

    private String assemblyContentMessage(String from, String message) {
        return String.format("%s said:%s", from, message);
    }

    private Map<String, Object> getChatbotErrorExt() {
        Map<String, Object> ext = new HashMap<>();
        ext.put("chatbotError", true);
        return ext;
    }
}
