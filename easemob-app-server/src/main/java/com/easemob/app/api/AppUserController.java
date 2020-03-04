package com.easemob.app.api;

import com.easemob.app.model.*;
import com.easemob.app.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/app/chat/user/register")
    public ResponseEntity registerWithChatUser(@RequestBody @Valid AppUser appUser) {
        ResponseParam responseParam = new ResponseParam();

        this.appUserService.registerWithChatUser(appUser);

        responseParam.setCode(ResCode.RES_OK.code);
        return ResponseEntity.ok(responseParam);
    }

    @PostMapping("/app/chat/user/login")
    public ResponseEntity loginWithChatUser(@RequestBody @Valid AppUser appUser) {
        ResponseParam responseParam = new ResponseParam();

        TokenInfo token = appUserService.loginWithChatUser(appUser);

        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTimestamp(token.getExpireTimestamp());
        responseParam.setChatUserName(appUser.getUserAccount());
        responseParam.setAgoraUid(token.getAgoraUid());

        return ResponseEntity.ok(responseParam);
    }
}
