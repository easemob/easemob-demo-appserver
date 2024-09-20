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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class AppUserServiceImpl implements AppUserService {

    @Value("${application.baseUri}")
    private String baseUri;

    @Value("#{'${easemob.1v1.video.default.avatar.chatfile.uuid.list}'.split(',')}")
    private List<String> oneToOneVideoDefaultAvatarChatfileUuidList;

    @Autowired
    private AssemblyService assemblyService;

    @Autowired
    private RestService restService;

    @Override public UserLoginResponse oneToOneVideoLogin(String appKey, LoginAppUser appUser) {
        String phoneNumber = appUser.getPhoneNumber();
        AppServerUtils.isPhoneNumber(phoneNumber);
        String interceptPhoneNumber = phoneNumber.substring(phoneNumber.length() - 6);

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

        AppUserOneToOneVideoInfo appUserOneToOneVideoInfo = this.assemblyService.getAppUserOneToOneVideoInfoFromDB(appKey, phoneNumber);
        String chatUserName;
        String chatUserPassword;
        String avatarUrl;
        String agoraUid;

        if (appUserOneToOneVideoInfo != null) {
            chatUserName = appUserOneToOneVideoInfo.getChatUserName();
            avatarUrl = appUserOneToOneVideoInfo.getAvatarUrl();
            agoraUid = appUserOneToOneVideoInfo.getAgoraUid();
        } else {
            chatUserName = this.assemblyService.generateUniqueChatUsername(appKey);
            chatUserPassword = chatUserName.substring(chatUserName.length() - 6);
            avatarUrl = randomAvatarUrl(appKey, oneToOneVideoDefaultAvatarChatfileUuidList);

            this.restService.registerChatUserName(appKey, chatUserName, chatUserPassword);
            MultiValueMap<String, Object> metadata = new LinkedMultiValueMap<>();
            metadata.add("avatarurl", avatarUrl);
            this.restService.setUserMetadata(appKey, chatUserName, metadata);

            agoraUid = this.assemblyService.generateUniqueAgoraUid(appKey);
            this.assemblyService.saveAppUserToOneToOneVideoToDB(appKey, phoneNumber, chatUserName,
                    agoraUid, avatarUrl);
        }

        String userToken =
                this.restService.getChatUserToken(appKey, chatUserName);

        UserLoginResponse response = new UserLoginResponse();
        response.setToken(userToken);
        response.setPhoneNumber(phoneNumber);
        response.setUserName(chatUserName);
        response.setAvatarUrl(avatarUrl);
        response.setAgoraUid(agoraUid);
        return response;
    }

    @Override
    public String oneToOneVideoUploadAvatar(String appkey, String phoneNumber, MultipartFile file) {
        AppUserOneToOneVideoInfo appUserOneToOneVideoInfo = this.assemblyService.getAppUserOneToOneVideoInfoFromDB(appkey, phoneNumber);
        if (appUserOneToOneVideoInfo == null) {
            throw new ASNotFoundException("The phone number not found.");
        }

        File chatFile = FileCovert.convertMultipartFileToFile(appkey, phoneNumber, file);
        String avatarUrl = restService.uploadFile(appkey, phoneNumber, chatFile);
        chatFile.delete();
        appUserOneToOneVideoInfo.setAvatarUrl(avatarUrl);
        appUserOneToOneVideoInfo.setUpdatedAt(LocalDateTime.now());
        assemblyService.updateAppUserToOneToOneVideoToDB(appUserOneToOneVideoInfo);
        return avatarUrl;
    }

    private String randomAvatarUrl(String appKey, List<String> avatarChatfileUuidList) {
        String orgName = appKey.split("#")[0];
        String appName = appKey.split("#")[1];

        // 创建一个随机数生成器
        Random random = new Random();

        // 生成一个随机索引
        int randomIndex = random.nextInt(avatarChatfileUuidList.size());

        // 获取随机索引处的元素
        String randomElement = avatarChatfileUuidList.get(randomIndex);

        return baseUri + "/" + orgName + "/" + appName + "/chatfiles/" + randomElement;
    }
}
