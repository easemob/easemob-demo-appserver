package com.easemob.app.service.impl;

import com.easemob.app.exception.ASDuplicateUniquePropertyExistsException;
import com.easemob.app.exception.ASNotFoundException;
import com.easemob.app.exception.ASPasswordErrorException;
import com.easemob.app.model.AppUser;
import com.easemob.app.model.AppUserInfo;
import com.easemob.app.model.TokenInfo;
import com.easemob.app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AssemblyService assemblyService;

    @Autowired
    private ServerSDKService sdkService;

    @Autowired
    private RestService restService;

    @Value("${application.appkey}")
    private String defaultAppkey;

    @Override
    public void registerWithChatUser(AppUser appUser) {
        String chatUserName = appUser.getUserAccount().toLowerCase();
        String chatUserPassword = appUser.getUserPassword();

        if (this.assemblyService.checkIfUserAccountExistsDB(defaultAppkey, chatUserName)) {
            throw new ASDuplicateUniquePropertyExistsException("userAccount " + chatUserName + " already exists");
        } else {
            if (!this.sdkService.checkIfChatUserNameExists(chatUserName)) {
                this.sdkService.registerChatUserName(chatUserName);
            }

            this.assemblyService.saveAppUserToDB(defaultAppkey, chatUserName, null, chatUserPassword, chatUserName, this.assemblyService.generateUniqueAgoraUid(defaultAppkey));
        }
    }

    @Override
    public TokenInfo loginWithChatUser(AppUser appUser) {
        String userAccount = appUser.getUserAccount().toLowerCase();

        if (this.assemblyService.checkIfUserAccountExistsDB(defaultAppkey, userAccount)) {
            if (!this.sdkService.checkIfChatUserNameExists(userAccount)) {
                this.sdkService.registerChatUserName(userAccount);
            }

            AppUserInfo userInfo = this.assemblyService.getAppUserInfoFromDB(defaultAppkey, userAccount);
            if (userInfo.getUserPassword() == null) {
                this.assemblyService.updateAppUserToDB(defaultAppkey, userInfo.getId(), userInfo.getUserAccount(), userInfo.getUserNickname(), appUser.getUserPassword(), userInfo.getChatUserName(), userInfo.getAgoraUid());
            } else {
                if (!appUser.getUserPassword().equals(userInfo.getUserPassword())) {
                    throw new ASPasswordErrorException("userAccount password error");
                }
            }
        } else {
            throw new ASNotFoundException("userAccount " + userAccount + " does not exist");
        }

        return this.tokenService.getUserTokenWithAccount(defaultAppkey, userAccount);
    }

    @Override public void registerWithChatUserV1(String appkey, AppUser appUser) {
        String chatUserName = appUser.getUserAccount().toLowerCase();
        String chatUserPassword = appUser.getUserPassword();

        if (this.assemblyService.checkIfUserAccountExistsDB(appkey, chatUserName)) {
            throw new ASDuplicateUniquePropertyExistsException("userAccount " + chatUserName + " already exists");
        } else {
            if (!this.restService.checkIfChatUserNameExists(appkey, chatUserName)) {
                this.restService.registerChatUserName(appkey, chatUserName);
            }

            this.assemblyService.saveAppUserToDB(appkey, chatUserName, null, chatUserPassword, chatUserName, this.assemblyService.generateUniqueAgoraUid(appkey));
        }
    }

    @Override
    public TokenInfo loginWithChatUserV2(String appkey, AppUser appUser, String appId, String appCert) {
        String userAccount = appUser.getUserAccount().toLowerCase();

        if (this.assemblyService.checkIfUserAccountExistsDB(appkey, userAccount)) {
            if (!this.restService.checkIfChatUserNameExists(appkey, userAccount)) {
                this.restService.registerChatUserName(appkey, userAccount);
            }

            AppUserInfo userInfo = this.assemblyService.getAppUserInfoFromDB(appkey, userAccount);
            if (userInfo.getUserPassword() == null) {
                this.assemblyService.updateAppUserToDB(appkey, userInfo.getId(), userInfo.getUserAccount(), userInfo.getUserNickname(), appUser.getUserPassword(), userInfo.getChatUserName(), userInfo.getAgoraUid());
            } else {
                if (!appUser.getUserPassword().equals(userInfo.getUserPassword())) {
                    throw new ASPasswordErrorException("userAccount password error");
                }
            }
        } else {
            throw new ASNotFoundException("userAccount " + userAccount + " does not exist");
        }

        return this.tokenService.getUserTokenWithAccountV1(appkey, userAccount, appId, appCert);
    }

}
