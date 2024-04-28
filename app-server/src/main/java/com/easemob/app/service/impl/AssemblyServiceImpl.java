package com.easemob.app.service.impl;

import cn.hutool.core.lang.UUID;
import com.easemob.app.model.AppUserInfoNew;
import com.easemob.app.repository.AppUserInfoNewRepository;
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
    private AppUserInfoNewRepository appUserInfoNewRepository;

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

    @Override public AppUserInfoNew getAppUserInfoNewFromDB(String appKey, String phoneNumber) {
        return this.appUserInfoNewRepository.findByAppkeyAndPhoneNumber(appKey, phoneNumber);
    }

    @Override
    public AppUserInfoNew getAppUserInfoNewByChatUserName(String appKey, String chatUserName) {
        return this.appUserInfoNewRepository.findByAppkeyAndChatUserName(appKey, chatUserName);
    }

    @Override
    public boolean checkIfChatUsernameExistsDB(String appKey, String chatUsername) {
        AppUserInfoNew appUserInfo = this.appUserInfoNewRepository.findByAppkeyAndChatUserName(appKey, chatUsername);
        return appUserInfo != null;
    }

    @Override
    public boolean checkIfAgoraUidExistsDB(String appKey, String agoraUid) {
        AppUserInfoNew appUserInfo = this.appUserInfoNewRepository.findByAppkeyAndAgoraUid(appKey, agoraUid);
        return appUserInfo != null;
    }

    @Override public void saveAppUserNewToDB(String appKey, String phoneNumber, String chatUsername,
            String chatUserPassword, String agoraUid) {
        AppUserInfoNew appUserInfo = new AppUserInfoNew();
        appUserInfo.setAppkey(appKey);
        appUserInfo.setPhoneNumber(phoneNumber);
        appUserInfo.setChatUserName(chatUsername);
        appUserInfo.setChatUserPassword(chatUserPassword);
        appUserInfo.setAgoraUid(agoraUid);
        appUserInfo.setCreatedAt(LocalDateTime.now());
        appUserInfo.setUpdatedAt(LocalDateTime.now());
        this.appUserInfoNewRepository.save(appUserInfo);
        log.info("userAccount info new save to db successfully :{}", appUserInfo);
    }

    @Override public void updateAppUserInfoToDB(AppUserInfoNew appUserInfo) {
        this.appUserInfoNewRepository.save(appUserInfo);
        log.info("userAccount avatar url update to db successfully :{}", appUserInfo);
    }

}
