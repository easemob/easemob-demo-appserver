package com.easemob.agora.service.impl;

import com.easemob.agora.model.AppUser;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.AppUserService;
import com.easemob.agora.service.AssemblyService;
import com.easemob.agora.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AssemblyService assemblyService;

    @Override
    public boolean registerUser(AppUser appUser) {
        String userAccount = appUser.getUserAccount();
        if (this.assemblyService.checkIfUserAccountExistsDB(userAccount)) {
            return false;
        }
        this.assemblyService.registerUserAccount(appUser.getUserAccount(), appUser.getUserPassword());
        return true;
    }

    @Override
    public TokenInfo loginUser(AppUser appUser) {
        String userAccount = appUser.getUserAccount();
        if (!this.assemblyService.checkIfUserAccountExistsDB(userAccount)) {
            this.assemblyService.registerUserAccount(userAccount, appUser.getUserPassword());
        }
        return tokenService.getUserToken(userAccount);
    }

}
