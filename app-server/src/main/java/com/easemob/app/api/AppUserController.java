package com.easemob.app.api;

import com.easemob.app.model.*;
import com.easemob.app.service.AppUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
public class AppUserController {

    @Value("${application.1v1.video.appkey}")
    private String videoAppKey;

    @Autowired
    private AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/inside/app/user/1v1/video/login")
    public ResponseEntity oneToOneVideoLogin(@RequestBody @Valid LoginAppUser appUser) {
        ResponseParam responseParam = new ResponseParam();

        UserLoginResponse loginResponse = appUserService.oneToOneVideoLogin(videoAppKey, appUser);

        responseParam.setCode(ResCode.RES_OK.getCode());
        responseParam.setToken(loginResponse.getToken());
        responseParam.setPhoneNumber(loginResponse.getPhoneNumber());
        responseParam.setChatUserName(loginResponse.getUserName());
        responseParam.setAvatarUrl(loginResponse.getAvatarUrl());
        responseParam.setAgoraUid(loginResponse.getAgoraUid());
        return ResponseEntity.ok(responseParam);
    }

    @PostMapping(value = "/inside/app/user/{phoneNumber}/1v1/video/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity appUserOneToOneVideoAvatarUpload(@PathVariable("phoneNumber") String phoneNumber, MultipartFile file) {

        if (StringUtils.isBlank(phoneNumber)) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        if (file == null) {
            throw new IllegalArgumentException("File must be provided.");
        }

        ResponseParam responseParam = new ResponseParam();
        String url = appUserService.oneToOneVideoUploadAvatar(videoAppKey, phoneNumber, file);
        responseParam.setCode(ResCode.RES_OK.getCode());
        responseParam.setAvatarUrl(url);

        return ResponseEntity.ok(responseParam);
    }

}
