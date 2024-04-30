package com.easemob.app.service.impl;

import com.easemob.app.exception.*;
import com.easemob.app.model.*;
import com.easemob.app.service.*;
import com.easemob.app.utils.AppServerUtils;
import com.easemob.app.utils.FileCovert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class AppUserServiceImpl implements AppUserService {

    @Value("${application.appkey}")
    private String defaultAppKey;

    @Autowired
    private AssemblyService assemblyService;

    @Autowired
    private RestService restService;

    @Autowired
    private RedisService redisService;

    @Override
    public UserLoginResponse loginWithPhoneNumber(String appKey, LoginAppUser appUser) {
        String phoneNumber = appUser.getPhoneNumber();
        AppServerUtils.isPhoneNumber(phoneNumber);

        // 需要自己做短信验证码检查
//        List<Object> smsCodeList = redisService.getSmsCodeRecord(phoneNumber);
//        if (smsCodeList.isEmpty()) {
//            throw new IllegalArgumentException(
//                    "Please send SMS to get mobile phone verification code.");
//        } else {
//            AtomicBoolean mark = new AtomicBoolean(false);
//            smsCodeList.forEach(smsCode -> {
//                if (appUser.getSmsCode().equals(String.valueOf(smsCode))) {
//                    mark.set(true);
//                }
//            });
//
//            if (!mark.get()) {
//                throw new IllegalArgumentException("SMS verification code error.");
//            }
//        }

        AppUserInfoNew appUserInfo = this.assemblyService.getAppUserInfoNewFromDB(appKey, phoneNumber);
        String chatUserName;
        String chatUserPassword;
        String avatarUrl = null;
        if (appUserInfo != null) {
            chatUserName = appUserInfo.getChatUserName();
            chatUserPassword = appUserInfo.getChatUserPassword();
            avatarUrl = appUserInfo.getAvatarUrl();
        } else {
            chatUserName = this.assemblyService.generateUniqueChatUsername(appKey);
            chatUserPassword = chatUserName.substring(chatUserName.length() - 6);

            this.restService.registerChatUserName(appKey, chatUserName, chatUserPassword);
            this.assemblyService.saveAppUserNewToDB(appKey, phoneNumber, chatUserName,
                    chatUserPassword,
                    this.assemblyService.generateUniqueAgoraUid(appKey));
        }

        String userToken =
                this.restService.getChatUserToken(appKey, chatUserName, chatUserPassword);

        UserLoginResponse response = new UserLoginResponse();
        response.setToken(userToken);
        response.setPhoneNumber(phoneNumber);
        response.setUserName(chatUserName);
        response.setAvatarUrl(avatarUrl);
        return response;
    }

    @Override public String uploadAvatar(String appkey, String chatUsername, MultipartFile file) {
        AppUserInfoNew appUserInfo = this.assemblyService.getAppUserInfoNewByChatUserName(appkey, chatUsername);
        if (appUserInfo == null) {
            throw new ASNotFoundException("The chat username not found.");
        }

        File chatFile = FileCovert.convertMultipartFileToFile(appkey, chatUsername, file);
        String avatarUrl = restService.uploadFile(defaultAppKey, chatUsername, chatFile);
        chatFile.delete();
        appUserInfo.setAvatarUrl(avatarUrl);
        appUserInfo.setUpdatedAt(LocalDateTime.now());
        assemblyService.updateAppUserInfoToDB(appUserInfo);
        return avatarUrl;
    }
}
