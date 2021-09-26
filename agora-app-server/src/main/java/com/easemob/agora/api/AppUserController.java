package com.easemob.agora.api;

import com.easemob.agora.exception.ASGetTokenReachedLimitException;
//import com.easemob.agora.limit.AppUserLimitService;
import com.easemob.agora.model.AppUser;
import com.easemob.agora.model.ResCode;
import com.easemob.agora.model.ResponseParam;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

//    @Autowired
//    private AppUserLimitService appUserLimitService;

//    @Value("${spring.redis.get.token.limit.count}")
//    private int getTokenLimitCount;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/app/user/register")
    public ResponseParam register(@RequestBody AppUser appUser) {
        ResponseParam responseParam = new ResponseParam();

        if (appUserService.registerUser(appUser)) {
            responseParam.setCode(ResCode.RES_OK);
        } else {
            responseParam.setCode(ResCode.RES_USER_ALREADY_EXISTS);
            responseParam.setErrorInfo(String.format("%s already exists", appUser.getUserAccount()));
        }
        return responseParam;
    }

    @PostMapping("/app/user/login")
    public ResponseParam login(@RequestBody AppUser appUser) {
//        if (appUserLimitService.getTokenReachedLimit(appUser.getUserAccount()) > getTokenLimitCount) {
//            throw new ASGetTokenReachedLimitException("get token reached limit");
//        }

        TokenInfo token = appUserService.loginUser(appUser);
        ResponseParam responseParam = new ResponseParam();
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTimestamp(token.getExpireTimestamp());
        responseParam.setEasemobUserName(token.getEasemobUserName());
        responseParam.setAgoraUid(token.getAgoraUid());
        return responseParam;
    }

}
