package com.easemob.app.service.impl;

import com.easemob.app.exception.ASDuplicateUniquePropertyExistsException;
import com.easemob.app.exception.ASPasswordErrorException;
import com.easemob.app.model.AppUser;
import com.easemob.app.model.AppUserInfo;
import com.easemob.app.model.TokenInfo;
import com.easemob.app.service.AppUserService;
import com.easemob.app.service.AssemblyService;
import com.easemob.app.service.ServerSDKService;
import com.easemob.app.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AssemblyService assemblyService;

    @Autowired
    private ServerSDKService sdkService;

    @Override
    public void registerWithChatUser(AppUser appUser) {
        String chatUserName = appUser.getUserAccount();
        String chatUserPassword = appUser.getUserPassword();

        if (this.assemblyService.checkIfUserAccountExistsDB(chatUserName)) {
            throw new ASDuplicateUniquePropertyExistsException("userAccount " + chatUserName + " already exists");
        } else {
            if (this.sdkService.checkIfChatUserNameExists(chatUserName)) {
                throw new ASDuplicateUniquePropertyExistsException("chatUserName " + chatUserName + " already exists");
            } else {
                this.sdkService.registerChatUserName(chatUserName);
                this.assemblyService.saveAppUserToDB(chatUserName, null, chatUserPassword, chatUserName, this.assemblyService.generateUniqueAgoraUid());
            }
        }
    }

    @Override
    public TokenInfo loginWithChatUser(AppUser appUser) {
        String userAccount = appUser.getUserAccount();

        if (!this.assemblyService.checkIfUserAccountExistsDB(userAccount)) {
            if (this.sdkService.checkIfChatUserNameExists(userAccount)) {
                throw new ASDuplicateUniquePropertyExistsException("chatUserName " + userAccount + " already exists");
            }
        } else {
            AppUserInfo userInfo = this.assemblyService.getAppUserInfoFromDB(userAccount);
            if (!appUser.getUserPassword().equals(userInfo.getUserPassword())) {
                throw new ASPasswordErrorException("user password error");
            }
        }

        return this.tokenService.getUserTokenWithAccount(userAccount);
    }

}
