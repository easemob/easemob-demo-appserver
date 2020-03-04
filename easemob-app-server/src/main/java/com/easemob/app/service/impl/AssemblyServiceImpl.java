package com.easemob.app.service.impl;

import com.easemob.app.exception.ASDuplicateUniquePropertyExistsException;
import com.easemob.app.model.AppUserInfo;
import com.easemob.app.model.AppUserInfoRepository;
import com.easemob.app.service.AssemblyService;
import com.easemob.app.utils.RandomUidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AssemblyServiceImpl implements AssemblyService {

    @Autowired
    private AppUserInfoRepository appUserInfoRepository;

    @Override
    public String generateUniqueAgoraUid(String appkey) {
        String uid = RandomUidUtil.getUid();
        while (true) {
            if (checkIfAgoraUidExistsDB(appkey, uid)) {
                uid = RandomUidUtil.getUid();
            } else {
                break;
            }
        }
        return uid;
    }

    @Override
    public AppUserInfo getAppUserInfoFromDB(String appkey, String userAccount) {
        return this.appUserInfoRepository.findByAppKeyAndUserAccount(appkey, userAccount);
    }

    @Override
    public boolean checkIfUserAccountExistsDB(String appkey, String userAccount) {
        AppUserInfo appUserInfo = this.appUserInfoRepository.findByAppKeyAndUserAccount(appkey, userAccount);
        return appUserInfo != null;
    }

    @Override
    public boolean checkIfAgoraUidExistsDB(String appkey, String agoraUid) {
        AppUserInfo appUserInfo = this.appUserInfoRepository.findByAppKeyAndAgoraUid(appkey, agoraUid);
        return appUserInfo != null;
    }

    @Override
    public void saveAppUserToDB(String appkey, String userAccount, String userNickname, String userPassword, String chatUserName, String agoraUid) {
        AppUserInfo appUserInfo = new AppUserInfo();
        appUserInfo.setAppkey(appkey);
        appUserInfo.setUserAccount(userAccount);
        appUserInfo.setUserNickname(userNickname);
        appUserInfo.setUserPassword(userPassword);
        appUserInfo.setChatUserName(chatUserName);
        appUserInfo.setAgoraUid(agoraUid);

        try {
            this.appUserInfoRepository.save(appUserInfo);
        } catch (Exception e) {
            log.error("save appUserInfo to db failed : {}", e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new ASDuplicateUniquePropertyExistsException("userAccount " + userAccount + " already exists");
            }
        }

        log.info("userAccount info save to db successfully :{}", userAccount);
    }

    @Override
    public void updateAppUserToDB(String appkey, Long id, String userAccount, String userNickname, String userPassword, String chatUserName, String agoraUid) {
        AppUserInfo appUserInfo = new AppUserInfo();
        appUserInfo.setId(id);
        appUserInfo.setAppkey(appkey);
        appUserInfo.setUserAccount(userAccount);
        appUserInfo.setUserNickname(userNickname);
        appUserInfo.setUserPassword(userPassword);
        appUserInfo.setChatUserName(chatUserName);
        appUserInfo.setAgoraUid(agoraUid);
        this.appUserInfoRepository.save(appUserInfo);
        log.info("userAccount info save to db successfully :{}", userAccount);
    }

}
