package com.easemob.app.api;

import com.easemob.app.exception.ASUnAuthorizedException;
import com.easemob.app.model.*;
import com.easemob.app.service.AppUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Value("#{'${application.appkey.white.list}'.split(',')}")
    private List<String> appkeyWhiteList;

    @Value("#{'${application.agoraAppId.white.list}'.split(',')}")
    private List<String> appIdWhiteList;

    @Value("#{'${application.agoraCert.white.list}'.split(',')}")
    private List<String> appCertWhiteList;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    // 仅 agora 客户端 chat demo 使用, 开发者无需关注此 api
    @PostMapping("/app/chat/user/register")
    public ResponseEntity registerWithChatUser(@RequestBody @Valid AppUser appUser) {
        ResponseParam responseParam = new ResponseParam();

        this.appUserService.registerWithChatUser(appUser);

        responseParam.setCode(ResCode.RES_OK);
        return ResponseEntity.ok(responseParam);
    }

    // 仅 agora 客户端 chat demo 使用, 开发者无需关注此 api
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

    // 仅 agora 客户端 chat demo 使用, 开发者无需关注此 api
    @PostMapping("/{orgName}/{appName}/app/chat/user/register")
    public ResponseEntity registerWithChatUserV1(
            @PathVariable(name = "orgName") String orgName,
            @PathVariable(name = "appName") String appName,
            @RequestBody @Valid AppUser appUser) {

        checkAuth(orgName, appName);
        ResponseParam responseParam = new ResponseParam();
        String appkey = orgName + "#" + appName;

        this.appUserService.registerWithChatUserV1(appkey, appUser);
        responseParam.setCode(ResCode.RES_OK);
        return ResponseEntity.ok(responseParam);
    }

    // 仅 agora 客户端 chat demo 使用, 开发者无需关注此 api
    @PostMapping("/{orgName}/{appName}/app/chat/user/login")
    public ResponseEntity loginWithChatUserV1(
            @PathVariable(name = "orgName") String orgName,
            @PathVariable(name = "appName") String appName,
            @RequestBody @Valid AppUser appUser) {

        int index = checkAuth(orgName, appName);
        ResponseParam responseParam = new ResponseParam();
        String appkey = orgName + "#" + appName;

        TokenInfo token = appUserService.loginWithChatUserV2(appkey, appUser, appIdWhiteList.get(index), appCertWhiteList.get(index));

        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTimestamp(token.getExpireTimestamp());
        responseParam.setChatUserName(appUser.getUserAccount());
        responseParam.setAgoraUid(token.getAgoraUid());

        return ResponseEntity.ok(responseParam);
    }

    private int checkAuth(String orgName, String appName) {
        if (StringUtils.isBlank(orgName) || StringUtils.isBlank(appName)) {
            throw new IllegalArgumentException("OrgName or AppName is not empty.");
        }

        if (appkeyWhiteList == null || appkeyWhiteList.size() == 0) {
            throw new ASUnAuthorizedException("The appkey has no operation permission.");
        }

        String appkey = orgName + "#" + appName;
        boolean flag = false;
        int index = 0;
        for (int i = 0; i < appkeyWhiteList.size(); i++) {
            if (appkey.equals(appkeyWhiteList.get(i))) {
                index = i;
                flag = true;
                break;
            }
        }

        if (!flag) {
            throw new ASUnAuthorizedException("The appkey has no operation permission.");
        }

        return index;
    }
}
