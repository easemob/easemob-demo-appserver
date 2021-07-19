package com.easemob.agora.service.impl;

import com.easemob.agora.model.AppUserInfo;
import com.easemob.agora.model.AppUserInfoRepository;
import com.easemob.agora.service.AssemblyService;
import com.easemob.agora.service.ServerSDKService;
import com.easemob.agora.utils.RandomUidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AssemblyServiceImpl implements AssemblyService {

    private static final String EASEMOB_USER_NAME_PREFIX = "em";

    @Autowired
    private ServerSDKService serverSDKService;

    @Autowired
    private AppUserInfoRepository appUserInfoRepository;

    @Override
    public void registerUserAccount(String userAccount, String userPassword) {
        String agoraUid = generateUniqueAgoraUid();
        String easemobUserName = EASEMOB_USER_NAME_PREFIX + agoraUid;
        while (true) {
            if (this.serverSDKService.checkIfEasemobUserNameExists(easemobUserName)) {
                agoraUid = generateUniqueAgoraUid();
                easemobUserName = EASEMOB_USER_NAME_PREFIX + agoraUid;
            } else {
                break;
            }
        }
        this.serverSDKService.registerEasemobUserName(easemobUserName);
        String easemobUserId = this.serverSDKService.getEasemobUserId(easemobUserName);
        saveAppUserToDB(userAccount, userPassword, easemobUserName, easemobUserId, agoraUid);
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
    public void saveAppUserToDB(String userAccount, String userPassword, String easemobUserName, String easemobUserId, String agoraUid) {
        AppUserInfo appUserInfo = new AppUserInfo();
        appUserInfo.setUserAccount(userAccount);
        appUserInfo.setUserPassword(userPassword);
        appUserInfo.setEasemobUserName(easemobUserName);
        appUserInfo.setEasemobUserId(easemobUserId);
        appUserInfo.setAgoraUid(agoraUid);
        this.appUserInfoRepository.save(appUserInfo);
        log.info("userAccount info save to db successfully :{}", userAccount);
    }

    @Override
    public AppUserInfo checkAppUserInfo(AppUserInfo appUserInfo) {
        String userAccount = appUserInfo.getUserAccount();
        String easemobUserName = appUserInfo.getEasemobUserName();
        String easemobUserId = appUserInfo.getEasemobUserId();
        String agoraUid = appUserInfo.getAgoraUid();
        if (Strings.isNotBlank(easemobUserName) && Strings.isNotBlank(easemobUserId) && Strings.isNotBlank(agoraUid)) {
            if (!this.serverSDKService.checkIfEasemobUserNameExists(easemobUserName)) {
                this.serverSDKService.registerEasemobUserName(easemobUserName);
                easemobUserId = this.serverSDKService.getEasemobUserId(easemobUserName);
                appUserInfo.setEasemobUserId(easemobUserId);
                this.appUserInfoRepository.save(appUserInfo);
            }
        } else {
            if (Strings.isBlank(agoraUid)) {
                agoraUid = generateUniqueAgoraUid();
            }

            if (Strings.isBlank(easemobUserName) || Strings.isBlank(easemobUserId)) {
                easemobUserName = EASEMOB_USER_NAME_PREFIX + agoraUid;
                if (!this.serverSDKService.checkIfEasemobUserNameExists(easemobUserName)) {
                    this.serverSDKService.registerEasemobUserName(easemobUserName);
                }
                easemobUserId = this.serverSDKService.getEasemobUserId(easemobUserName);
            }

            appUserInfo.setEasemobUserName(easemobUserName);
            appUserInfo.setEasemobUserId(easemobUserId);
            appUserInfo.setAgoraUid(agoraUid);
            this.appUserInfoRepository.save(appUserInfo);
            log.info("appUserInfo update to db successfully : {}, {}, {}, {}", userAccount, easemobUserName, easemobUserId, agoraUid);
        }
        return appUserInfo;
    }
}
