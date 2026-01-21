package com.easemob.app.api;

import com.easemob.app.model.request.*;
import com.easemob.app.model.response.*;
import com.easemob.app.model.enums.*;
import com.easemob.app.service.AppUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
public class AppUserController {

    @Value("${application.appkey}")
    private String defaultAppKey;

    @Autowired
    private AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/inside/app/user/login/V2")
    public ResponseEntity<ResponseParam> loginWithPhoneNumber(@RequestBody @Valid LoginAppUser appUser) {

        ResponseParam responseParam = new ResponseParam();

        UserLoginResponse loginResponse = appUserService.loginWithPhoneNumber(defaultAppKey, appUser);

        responseParam.setCode(ResCode.RES_OK.getCode());
        responseParam.setToken(loginResponse.getToken());
        responseParam.setPhoneNumber(loginResponse.getPhoneNumber());
        responseParam.setChatUserName(loginResponse.getUserName());
        responseParam.setAvatarUrl(loginResponse.getAvatarUrl());
        return ResponseEntity.ok(responseParam);
    }

    @PostMapping(value = "/inside/app/user/{chatUsername}/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseParam> appUserAvatarUpload(@PathVariable("chatUsername") String chatUsername, MultipartFile file) {

        if (StringUtils.isBlank(chatUsername)) {
            throw new IllegalArgumentException("Chat username cannot be empty.");
        }

        if (file == null) {
            throw new IllegalArgumentException("File must be provided.");
        }

        ResponseParam responseParam = new ResponseParam();
        String url = appUserService.uploadAvatar(defaultAppKey, chatUsername, file);
        responseParam.setCode(ResCode.RES_OK.getCode());
        responseParam.setAvatarUrl(url);

        return ResponseEntity.ok(responseParam);
    }

}
