package com.easemob.app.service;

import com.easemob.app.config.AppConstants;
import com.easemob.app.model.AppUserOneToOneVideoInfo;
import com.easemob.app.model.AppUserPresenceStatus;
import com.easemob.app.repository.AppUserOneToOneVideoInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class PresenceService {

    @Value("${application.1v1.video.appkey}")
    private String videoAppKey;

    @Autowired
    private AppUserOneToOneVideoInfoRepository appUserOneToOneVideoInfoRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RestService restService;

    @PostConstruct
    public void startAsyncTask() {
        // 创建异步任务
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Long id = 0L;
            while (true) {
                List<AppUserOneToOneVideoInfo> appUserOneToOneVideoInfos = appUserOneToOneVideoInfoRepository.findAppUsers(videoAppKey, id, 100);
                List<String> usernames = new ArrayList<>();
                appUserOneToOneVideoInfos.forEach(appUserOneToOneVideoInfo -> {
                    usernames.add(appUserOneToOneVideoInfo.getChatUserName());
                });

                if (usernames.size() != 0) {
                    List<AppUserPresenceStatus> userPresenceStatuses;
                    try {
                        userPresenceStatuses = restService.getUserPresenceStatus(videoAppKey, usernames);
                    } catch (Exception e) {
                        log.error("get user presence | rest service error. e : {}", e.getMessage());
                        continue;
                    }

                    userPresenceStatuses.forEach(userPresenceStatus -> {
                        String username = userPresenceStatus.getUsername();
                        Boolean isOnline = userPresenceStatus.getOnlineStatus();
                        if (!isOnline) {
                            String userMatchStatus = redisService.getUserStatus(videoAppKey, username, AppConstants.USER_MATCH_STATUS);
                            if (!AppConstants.ONE_TO_ONE_VIDEO_UNMATCH.equals(userMatchStatus)) {
                                redisService.removeUserToMatchList(videoAppKey, username);
                                String matchedUser = redisService.getUserStatus(videoAppKey, username, AppConstants.USER_MATCHED_CHAT_USER);
                                if (matchedUser != null) {
                                    redisService.setUserStatus(videoAppKey, matchedUser, AppConstants.USER_MATCH_STATUS, AppConstants.ONE_TO_ONE_VIDEO_UNMATCH);
                                    redisService.setUserStatus(videoAppKey, matchedUser, AppConstants.USER_MATCHED_CHAT_USER, null);

                                    Map<String, Object> messageExt = new HashMap<>();
                                    messageExt.put(AppConstants.USER_MATCHED_CHAT_USER, username);
                                    restService.sendCmdMessageToUser(videoAppKey, AppConstants.MESSAGE_ADMIN, matchedUser, AppConstants.MESSAGE_ONE_TO_ONE_VIDEO_CANCEL_MATCHED, true, messageExt);
                                }
                                redisService.setUserStatus(videoAppKey, username, AppConstants.USER_MATCH_STATUS, AppConstants.ONE_TO_ONE_VIDEO_UNMATCH);
                                redisService.setUserStatus(videoAppKey, username, AppConstants.USER_MATCHED_CHAT_USER, null);
                            }
                        }
                    });
                }

                if (appUserOneToOneVideoInfos.size() == 0 || appUserOneToOneVideoInfos.size() < 100) {
                    id = 0L;
                } else {
                    id = appUserOneToOneVideoInfos.get(appUserOneToOneVideoInfos.size() - 1).getId();
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error("get user presence | thread sleep error. e : {}", e.getMessage());
                }
            }
        });
    }

}
