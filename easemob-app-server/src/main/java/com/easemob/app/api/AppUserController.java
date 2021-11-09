package com.easemob.app.api;

import com.easemob.app.model.*;
import com.easemob.app.service.AppUserService;
import org.junit.platform.commons.util.ClassLoaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.*;

@RestController
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    // 仅 agora 客户端 chat demo 使用, 开发者无需关注此 api
//    @PostMapping("/app/chat/user/register")
//    public ResponseEntity registerWithChatUser(@RequestBody @Valid AppUser appUser) {
//        ResponseParam responseParam = new ResponseParam();
//
//        this.appUserService.registerWithChatUser(appUser);
//        responseParam.setCode(ResCode.RES_OK);
//        return ResponseEntity.ok(responseParam);
//    }

    // 仅 agora 客户端 chat demo 使用, 开发者无需关注此 api
//    @PostMapping("/app/chat/user/login")
//    public ResponseEntity loginWithChatUser(@RequestBody @Valid AppUser appUser) {
//        ResponseParam responseParam = new ResponseParam();
//
//        TokenInfo token = appUserService.loginWithChatUser(appUser);
//        responseParam.setAccessToken(token.getToken());
//        responseParam.setExpireTimestamp(token.getExpireTimestamp());
//        responseParam.setChatUserName(appUser.getUserAccount());
//        responseParam.setAgoraUid(token.getAgoraUid());
//
//        return ResponseEntity.ok(responseParam);
//    }

    // 仅 agora 客户端 chat demo 使用, 开发者无需关注此 api
    @PostMapping("/app/chat/user/login")
    public ResponseEntity loginWithChatUser(@RequestBody @Valid AppUserWithNickname appUser) {
        ResponseParam responseParam = new ResponseParam();

        TokenInfo token = appUserService.loginWithChatUser(appUser);
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTimestamp(token.getExpireTimestamp());
        responseParam.setChatUserName(appUser.getUserAccount());
        responseParam.setChatUserNickname(appUser.getUserNickname());
        responseParam.setAgoraUid(token.getAgoraUid());

        return ResponseEntity.ok(responseParam);
    }
}
