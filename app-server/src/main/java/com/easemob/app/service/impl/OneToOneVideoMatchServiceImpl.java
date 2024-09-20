package com.easemob.app.service.impl;

import com.easemob.app.config.AppConstants;
import com.easemob.app.exception.ASAuthException;
import com.easemob.app.exception.ASNotFoundException;
import com.easemob.app.exception.ASOneToOneVideoMatchException;
import com.easemob.app.model.AppUserOneToOneVideoInfo;
import com.easemob.app.model.OneToOneVideoMatchInfo;
import com.easemob.app.model.TokenInfo;
import com.easemob.app.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OneToOneVideoMatchServiceImpl implements OneToOneVideoMatchService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RestService restService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AssemblyService assemblyService;

    @Override
    public OneToOneVideoMatchInfo matchUser(String appkey, String phoneNumber, boolean sendCancelMatchNotify, String token) {
        AppUserOneToOneVideoInfo appUserOneToOneVideoInfo = assemblyService.getAppUserOneToOneVideoInfoFromDB(appkey, phoneNumber);
        if (appUserOneToOneVideoInfo == null) {
            throw new ASNotFoundException("App user does not exist.");
        }

        String chatUsername = appUserOneToOneVideoInfo.getChatUserName();

        boolean result = this.restService.checkUserTokenPermissions(appkey, chatUsername, token);
        if (!result) {
            throw new ASAuthException("No permission to match user.");
        }

        Boolean isMatchLock = redisService.matchLock(appkey, chatUsername);
        if (!Boolean.TRUE.equals(isMatchLock)) {
            throw new ASOneToOneVideoMatchException("Matching, please wait a moment");
        }

        String matchedUser = handleUnMatchUser(appkey, chatUsername, sendCancelMatchNotify);
        if (matchedUser != null) {
            redisService.setUserStatus(appkey, matchedUser, AppConstants.USER_MATCH_STATUS, AppConstants.ONE_TO_ONE_VIDEO_MATCHING);
            redisService.addUserToMatchList(appkey, matchedUser);
        }

        redisService.setUserStatus(appkey, chatUsername, AppConstants.USER_MATCH_STATUS, AppConstants.ONE_TO_ONE_VIDEO_MATCHING);
        redisService.addUserToMatchList(appkey, chatUsername);

        String randomMatchUser = null;

        while (true) {
            // 获取锁
            if (redisService.randomMatchUserLock(appkey)) {
                // 需要检查是否已有匹配数据，如果有就不在匹配，因为有可能其他人已经匹配到了自己
                String matchStatus = redisService.getUserStatus(appkey, chatUsername, AppConstants.USER_MATCH_STATUS);
                if (AppConstants.ONE_TO_ONE_VIDEO_MATCHED.equals(matchStatus)) {
                    randomMatchUser = redisService.getUserStatus(appkey, chatUsername, AppConstants.USER_MATCHED_CHAT_USER);
                    AppUserOneToOneVideoInfo matchedAppUser = assemblyService.getAppUserOneToOneVideoInfoByChatUsername(appkey, randomMatchUser);

                    String channelName = randomMatchUser + "-" + chatUsername;
                    TokenInfo tokenInfo = tokenService.getRtcToken(channelName, Integer.parseInt(appUserOneToOneVideoInfo.getAgoraUid()));
                    OneToOneVideoMatchInfo info = OneToOneVideoMatchInfo.builder()
                            .channelName(channelName)
                            .rtcToken(tokenInfo.getToken())
                            .agoraUid(appUserOneToOneVideoInfo.getAgoraUid())
                            .matchedUser(matchedAppUser.getPhoneNumber())
                            .matchedChatUser(randomMatchUser)
                            .build();

                    redisService.matchUnLock(appkey, chatUsername);
                    redisService.randomMatchUserUnLock(appkey);

                    log.info("1v1 video matched success: appkey : {}, phoneNumber : {}, matchedUser : {}", appkey, phoneNumber, matchedAppUser.getPhoneNumber());
                    return info;
                }

                // 获取随机匹配用户
                randomMatchUser = redisService.randomMatchUser(appkey, chatUsername);
                if (randomMatchUser != null) {
                    redisService.removeUserToMatchList(appkey, chatUsername);
                    redisService.removeUserToMatchList(appkey, randomMatchUser);
                }

                // 释放锁
                redisService.randomMatchUserUnLock(appkey);
                break;
            } else {
                // 等待一段时间，然后重新尝试获取锁
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
            }
        }

        if (randomMatchUser == null) {
            redisService.matchUnLock(appkey, chatUsername);
            redisService.setUserStatus(appkey, chatUsername, AppConstants.USER_MATCH_STATUS, AppConstants.ONE_TO_ONE_VIDEO_UNMATCH);
            throw new ASOneToOneVideoMatchException("no user matched");
        }

        redisService.setUserStatus(appkey, chatUsername, AppConstants.USER_MATCH_STATUS, AppConstants.ONE_TO_ONE_VIDEO_MATCHED);
        redisService.setUserStatus(appkey, chatUsername, AppConstants.USER_MATCHED_CHAT_USER, randomMatchUser);

        redisService.setUserStatus(appkey, randomMatchUser, AppConstants.USER_MATCH_STATUS, AppConstants.ONE_TO_ONE_VIDEO_MATCHED);
        redisService.setUserStatus(appkey, randomMatchUser, AppConstants.USER_MATCHED_CHAT_USER, chatUsername);

        redisService.matchUnLock(appkey, chatUsername);

        String channelName = chatUsername + "-" + randomMatchUser;
        String agoraUid = appUserOneToOneVideoInfo.getAgoraUid();

        TokenInfo tokenInfo = tokenService.getRtcToken(channelName, Integer.parseInt(agoraUid));
        redisService.saveAgoraChannelInfo(false, channelName, agoraUid);
        redisService.saveUidMapper(agoraUid, chatUsername);

        AppUserOneToOneVideoInfo matchedAppUser = assemblyService.getAppUserOneToOneVideoInfoByChatUsername(appkey, randomMatchUser);

        // 需要给被匹配到的用户发送cmd消息
        Map<String, Object> messageExt = new HashMap<>();
        messageExt.put("channelName", channelName);
        messageExt.put("agoraUid", matchedAppUser.getAgoraUid());
        messageExt.put("rtcToken", tokenService.getRtcToken(channelName, Integer.parseInt(matchedAppUser.getAgoraUid())).getToken());
        messageExt.put("matchedUser", phoneNumber);
        messageExt.put("matchedChatUser", chatUsername);

        restService.sendCmdMessageToUser(appkey, AppConstants.MESSAGE_ADMIN, randomMatchUser, AppConstants.MESSAGE_ONE_TO_ONE_VIDEO_MATCHED, true, messageExt);
        redisService.saveAgoraChannelInfo(false, channelName, matchedAppUser.getAgoraUid());
        redisService.saveUidMapper(matchedAppUser.getAgoraUid(), chatUsername);

        log.info("1v1 video matched success: appkey : {}, phoneNumber : {}, matchedUser : {}", appkey, phoneNumber, matchedAppUser.getPhoneNumber());

        return OneToOneVideoMatchInfo.builder()
                .channelName(channelName)
                .rtcToken(tokenInfo.getToken())
                .agoraUid(agoraUid)
                .matchedUser(matchedAppUser.getPhoneNumber())
                .matchedChatUser(randomMatchUser)
                .build();
    }

    @Override public void unMatchUser(String appkey, String phoneNumber, String token) {
        AppUserOneToOneVideoInfo appUserOneToOneVideoInfo = assemblyService.getAppUserOneToOneVideoInfoFromDB(appkey, phoneNumber);
        if (appUserOneToOneVideoInfo == null) {
            throw new ASNotFoundException("App user does not exist.");
        }

        String chatUsername = appUserOneToOneVideoInfo.getChatUserName();

        boolean result = this.restService.checkUserTokenPermissions(appkey, chatUsername, token);
        if (!result) {
            throw new ASAuthException("No permission to unmatch user.");
        }

        String matchStatus = redisService.getUserStatus(appkey, chatUsername, AppConstants.USER_MATCH_STATUS);
        if (matchStatus == null || AppConstants.ONE_TO_ONE_VIDEO_UNMATCH.equals(matchStatus) || AppConstants.ONE_TO_ONE_VIDEO_MATCHING.equals(matchStatus)) {
            throw new ASOneToOneVideoMatchException("User is not matched.");
        }

        redisService.removeUserToMatchList(appkey, chatUsername);
        String matchedUser = handleUnMatchUser(appkey, chatUsername, true);

        log.info("1v1 video unmatch success: appkey : {}, phoneNumber : {}, matchedUser : {}", appkey, phoneNumber, matchedUser);
    }

    private String handleUnMatchUser(String appkey, String chatUsername, boolean sendCancelMatchNotify) {
        String matchedUser = redisService.getUserStatus(appkey, chatUsername, AppConstants.USER_MATCHED_CHAT_USER);
        if (matchedUser != null) {
            redisService.setUserStatus(appkey, chatUsername, AppConstants.USER_MATCH_STATUS, AppConstants.ONE_TO_ONE_VIDEO_UNMATCH);
            redisService.setUserStatus(appkey, chatUsername, AppConstants.USER_MATCHED_CHAT_USER, null);
            redisService.setUserStatus(appkey, matchedUser, AppConstants.USER_MATCH_STATUS, AppConstants.ONE_TO_ONE_VIDEO_UNMATCH);
            redisService.setUserStatus(appkey, matchedUser, AppConstants.USER_MATCHED_CHAT_USER, null);

            if (sendCancelMatchNotify) {
                Map<String, Object> messageExt = new HashMap<>();
                messageExt.put(AppConstants.USER_MATCHED_CHAT_USER, chatUsername);
                restService.sendCmdMessageToUser(appkey, AppConstants.MESSAGE_ADMIN, matchedUser, AppConstants.MESSAGE_ONE_TO_ONE_VIDEO_CANCEL_MATCHED, true, messageExt);
            }
        }

        return matchedUser;
    }

    @Override public String getUserMatchStatus(String appkey, String chatUsername) {
        AppUserOneToOneVideoInfo appUserOneToOneVideoInfo = assemblyService.getAppUserOneToOneVideoInfoByChatUsername(appkey, chatUsername);
        if (appUserOneToOneVideoInfo == null) {
            throw new ASNotFoundException("App user does not exist.");
        }

        String matchStatus = redisService.getUserStatus(appkey, chatUsername, AppConstants.USER_MATCH_STATUS);

        if (matchStatus == null) {
            return AppConstants.ONE_TO_ONE_VIDEO_UNMATCH;
        }

        return matchStatus;
    }

}
