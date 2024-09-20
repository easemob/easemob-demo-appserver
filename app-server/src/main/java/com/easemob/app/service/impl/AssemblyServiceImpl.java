package com.easemob.app.service.impl;

import cn.hutool.core.lang.UUID;
import com.easemob.app.model.AppUserOneToOneVideoInfo;
import com.easemob.app.repository.AppUserOneToOneVideoInfoRepository;
import com.easemob.app.service.AssemblyService;
import com.easemob.app.utils.RandomUidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AssemblyServiceImpl implements AssemblyService {

    @Autowired
    private AppUserOneToOneVideoInfoRepository appUserOneToOneVideoInfoRepository;

    @Override
    public String generateUniqueAgoraUid(String appKey) {
        String uid = RandomUidUtils.getUid();
        while (true) {
            if (checkIfAgoraUidExistsDB(appKey, uid)) {
                uid = RandomUidUtils.getUid();
            } else {
                break;
            }
        }
        return uid;
    }

    @Override
    public String generateUniqueChatUsername(String appKey) {
        String chatUsername = UUID.fastUUID().toString(true).substring(0, 10);
        while (true) {
            if (checkIfChatUsernameExistsDB(appKey, chatUsername)) {
                chatUsername = UUID.fastUUID().toString(true).substring(0, 10);
            } else {
                break;
            }
        }
        return chatUsername;
    }

    @Override
    public boolean checkIfChatUsernameExistsDB(String appKey, String chatUsername) {
        AppUserOneToOneVideoInfo appUserInfo = this.appUserOneToOneVideoInfoRepository.findByAppkeyAndChatUserName(appKey, chatUsername);
        return appUserInfo != null;
    }

    @Override
    public boolean checkIfAgoraUidExistsDB(String appKey, String agoraUid) {
        AppUserOneToOneVideoInfo appUserInfo = this.appUserOneToOneVideoInfoRepository.findByAppkeyAndAgoraUid(appKey, agoraUid);
        return appUserInfo != null;
    }

    @Override
    public AppUserOneToOneVideoInfo getAppUserOneToOneVideoInfoFromDB(String appKey, String phoneNumber) {
        return this.appUserOneToOneVideoInfoRepository.findByAppkeyAndPhoneNumber(appKey, phoneNumber);
    }

    @Override
    public AppUserOneToOneVideoInfo getAppUserOneToOneVideoInfoByChatUsername(String appKey, String chatUsername) {
        return this.appUserOneToOneVideoInfoRepository.findByAppkeyAndChatUserName(appKey, chatUsername);
    }

    @Override
    public void saveAppUserToOneToOneVideoToDB(String appKey, String phoneNumber, String chatUsername,
            String agoraUid, String avatarUrl) {

        AppUserOneToOneVideoInfo appUserOneToOneVideoInfo = new AppUserOneToOneVideoInfo();
        appUserOneToOneVideoInfo.setAppkey(appKey);
        appUserOneToOneVideoInfo.setPhoneNumber(phoneNumber);
        appUserOneToOneVideoInfo.setChatUserName(chatUsername);
        appUserOneToOneVideoInfo.setAvatarUrl(avatarUrl);
        appUserOneToOneVideoInfo.setAgoraUid(agoraUid);
        appUserOneToOneVideoInfo.setUpdatedAt(LocalDateTime.now());
        appUserOneToOneVideoInfo.setCreatedAt(LocalDateTime.now());

        try {
            this.appUserOneToOneVideoInfoRepository.save(appUserOneToOneVideoInfo);
        } catch (Exception e) {
            log.error("app user 1v1 video save to db fail. appKey : {}, chatUsername : {}, error : {}", appKey, chatUsername, e.getMessage());
            throw new IllegalArgumentException("Save app group error.");
        }

        log.info("userAccount 1v1 video info save to db successfully :{}", appUserOneToOneVideoInfo);
    }

    @Override public void updateAppUserToOneToOneVideoToDB(AppUserOneToOneVideoInfo appUserOneToOneVideoInfo) {
        this.appUserOneToOneVideoInfoRepository.save(appUserOneToOneVideoInfo);
        log.info("userAccount 1v1 video update to db successfully :{}", appUserOneToOneVideoInfo);
    }

}
