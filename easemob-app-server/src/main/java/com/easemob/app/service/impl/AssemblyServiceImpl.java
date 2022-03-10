package com.easemob.app.service.impl;

import com.easemob.app.model.AppUserInfo;
import com.easemob.app.model.AppUserInfoRepository;
import com.easemob.app.service.AssemblyService;
import com.easemob.app.service.ServerSDKService;
import com.easemob.app.utils.RandomUidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AssemblyServiceImpl implements AssemblyService {

    private static final String CHAT_USER_NAME_PREFIX = "em";

    @Autowired
    private ServerSDKService serverSDKService;

    @Autowired
    private AppUserInfoRepository appUserInfoRepository;

    @Override
    public void registerUserAccount(String userAccount, String userPassword) {
        String agoraUid = generateUniqueAgoraUid();
        String chatUserName = CHAT_USER_NAME_PREFIX + agoraUid;
        while (true) {
            if (this.serverSDKService.checkIfChatUserNameExists(chatUserName)) {
                agoraUid = generateUniqueAgoraUid();
                chatUserName = CHAT_USER_NAME_PREFIX + agoraUid;
            } else {
                break;
            }
        }
        this.serverSDKService.registerChatUserName(chatUserName);
        saveAppUserToDB(userAccount, null, userPassword, chatUserName, agoraUid);
    }

    @Override
    public String generateUniqueAgoraUid() {
        String uid = RandomUidUtil.getUid();
        while (true) {
            if (checkIfAgoraUidExistsDB(uid)) {
                uid = RandomUidUtil.getUid();
            } else {
                break;
            }
        }
        return uid;
    }

    @Override
    public AppUserInfo getAppUserInfoFromDB(String userAccount) {
        return this.appUserInfoRepository.findByUserAccount(userAccount);
    }

    @Override
    public boolean checkIfUserAccountExistsDB(String userAccount) {
        AppUserInfo appUserInfo = this.appUserInfoRepository.findByUserAccount(userAccount);
        return appUserInfo != null;
    }

    @Override
    public boolean checkIfAgoraUidExistsDB(String agoraUid) {
        AppUserInfo appUserInfo = this.appUserInfoRepository.findByAgoraUid(agoraUid);
        return appUserInfo != null;
    }

    @Override
    public void saveAppUserToDB(String userAccount, String userNickname, String userPassword, String chatUserName, String agoraUid) {
        AppUserInfo appUserInfo = new AppUserInfo();
        appUserInfo.setUserAccount(userAccount);
        appUserInfo.setUserNickname(userNickname);
        appUserInfo.setUserPassword(userPassword);
        appUserInfo.setChatUserName(chatUserName);
        appUserInfo.setAgoraUid(agoraUid);
        this.appUserInfoRepository.save(appUserInfo);
        log.info("userAccount info save to db successfully :{}", userAccount);
    }

    @Override
    public AppUserInfo checkAppUserInfo(AppUserInfo appUserInfo) {
        String userAccount = appUserInfo.getUserAccount();
        String chatUserName = appUserInfo.getChatUserName();
        String agoraUid = appUserInfo.getAgoraUid();
        if (Strings.isNotBlank(chatUserName) && Strings.isNotBlank(agoraUid)) {
            if (!this.serverSDKService.checkIfChatUserNameExists(chatUserName)) {
                this.serverSDKService.registerChatUserName(chatUserName);
            }
        } else {
            if (Strings.isBlank(agoraUid)) {
                agoraUid = generateUniqueAgoraUid();
            }

            if (Strings.isBlank(chatUserName)) {
                chatUserName = CHAT_USER_NAME_PREFIX + agoraUid;
                if (!this.serverSDKService.checkIfChatUserNameExists(chatUserName)) {
                    this.serverSDKService.registerChatUserName(chatUserName);
                }
            }

            appUserInfo.setChatUserName(chatUserName);
            appUserInfo.setAgoraUid(agoraUid);
            this.appUserInfoRepository.save(appUserInfo);
            log.info("appUserInfo update to db successfully : {}, {}, {}", userAccount, chatUserName, agoraUid);
        }
        return appUserInfo;
    }
}
