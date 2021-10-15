package com.easemob.agora.service.impl;

import com.easemob.agora.exception.ASDuplicateUniquePropertyExistsException;
import com.easemob.agora.exception.ASPasswordErrorException;
import com.easemob.agora.model.AppUser;
import com.easemob.agora.model.AppUserInfo;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.AppUserService;
import com.easemob.agora.service.AssemblyService;
import com.easemob.agora.service.ServerSDKService;
import com.easemob.agora.service.TokenService;
import com.easemob.agora.utils.RestManger;
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

    @Autowired
    private RestManger restManger;

    @Override
    public void registerUser(AppUser appUser) {
        String userAccount = appUser.getUserAccount();
        if (this.assemblyService.checkIfUserAccountExistsDB(userAccount)) {
            throw new ASDuplicateUniquePropertyExistsException("userAccount " + userAccount + " already exists");
        }
        this.assemblyService.registerUserAccount(appUser.getUserAccount(), appUser.getUserPassword());
    }

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
                this.restManger.registerChatUser(chatUserName, chatUserPassword);
                this.assemblyService.saveAppUserToDB(chatUserName, chatUserPassword, chatUserName, this.assemblyService.generateUniqueAgoraUid());
            }
        }
    }

    @Override
    public TokenInfo loginUser(AppUser appUser) {
        String userAccount = appUser.getUserAccount();
        if (!this.assemblyService.checkIfUserAccountExistsDB(userAccount)) {
            this.assemblyService.registerUserAccount(userAccount, appUser.getUserPassword());
        } else {
            AppUserInfo userInfo = this.assemblyService.getAppUserInfoFromDB(userAccount);
            if (!appUser.getUserPassword().equals(userInfo.getUserPassword())) {
                throw new ASPasswordErrorException("user password error");
            }
        }
        return this.tokenService.getUserTokenWithAccount(userAccount);
    }

}
