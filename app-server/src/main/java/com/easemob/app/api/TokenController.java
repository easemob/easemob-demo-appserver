package com.easemob.app.api;

import com.easemob.app.model.ResCode;
import com.easemob.app.model.ResponseParam;
import com.easemob.app.model.TokenInfo;
import com.easemob.app.service.RedisService;
import com.easemob.app.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisService redisService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/token/rtc/channel/{channelName}/agorauid/{agoraUid}")
    public ResponseEntity getAgoraRtcToken(@PathVariable String channelName,
            @PathVariable Integer agoraUid,
            @RequestParam("userAccount") String userAccount) {

        ResponseParam responseParam = new ResponseParam();
        if (StringUtils.isBlank(channelName) || agoraUid == null) {
            responseParam.setCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());
            responseParam.setErrorInfo("channelName or agoraUid is not null.");
        }

        TokenInfo token = tokenService.getRtcToken(channelName, agoraUid);
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTimestamp(token.getExpireTimestamp());

        redisService.saveAgoraChannelInfo(false, channelName, String.valueOf(agoraUid));
        redisService.saveUidMapper(String.valueOf(agoraUid), userAccount);

        return ResponseEntity.ok(responseParam);
    }

    @GetMapping("/inside/token/rtc/channel/{channelName}/user/{chatUsername}")
    public ResponseEntity getInsideAgoraRtcToken(@PathVariable String channelName,
            @PathVariable String chatUsername) {

        ResponseParam responseParam = new ResponseParam();
        if (StringUtils.isBlank(channelName) || StringUtils.isBlank(chatUsername)) {
            responseParam.setCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());
            responseParam.setErrorInfo("channelName or chatUsername is not null.");
        }

        TokenInfo token = tokenService.getRtcToken(channelName);
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTimestamp(token.getExpireTimestamp());

        String agoraUid = token.getAgoraUid();
        redisService.saveAgoraChannelInfo(false, channelName, agoraUid);
        redisService.saveUidMapper(agoraUid, chatUsername);

        responseParam.setAgoraUid(agoraUid);

        return ResponseEntity.ok(responseParam);
    }

    @GetMapping("/inside/token/rtc/channel/{channelName}")
    public ResponseEntity getInsideAgoraRtcTokenV1(@PathVariable String channelName) {

        ResponseParam responseParam = new ResponseParam();
        if (StringUtils.isBlank(channelName)) {
            responseParam.setCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());
            responseParam.setErrorInfo("channelName is not null.");
        }

        TokenInfo token = tokenService.getRtcToken(channelName);
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTimestamp(token.getExpireTimestamp());
        responseParam.setAgoraUid(token.getAgoraUid());

        return ResponseEntity.ok(responseParam);
    }

    @GetMapping("/inside/token/rtc/channel/{channelName}/agorauid/{agoraUid}")
    public ResponseEntity getInsideAgoraRtcTokenV2(@PathVariable String channelName,
            @PathVariable Integer agoraUid) {

        ResponseParam responseParam = new ResponseParam();
        if (StringUtils.isBlank(channelName) || agoraUid == null) {
            responseParam.setCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());
            responseParam.setErrorInfo("channelName or agoraUid is not null");
        }

        TokenInfo token = tokenService.getRtcToken(channelName, agoraUid);
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTimestamp(token.getExpireTimestamp());

        return ResponseEntity.ok(responseParam);
    }

    @GetMapping("/inside/token/dynamic/{org}/{app}/users/{username}")
    public ResponseEntity getInsideDynamicToken(@PathVariable("org") String org,
            @PathVariable("app") String app,
            @PathVariable("username") String username,
            @RequestParam(name = "ttl", required = false, defaultValue = "600") Long ttl) {
        TokenInfo tokenInfo = tokenService.getDynamicToken(org, app, username, ttl);
        ResponseParam responseParam = new ResponseParam();
        responseParam.setAccessToken(tokenInfo.getToken());
        responseParam.setExpireTimestamp(tokenInfo.getExpireTimestamp());
        return ResponseEntity.ok(responseParam);
    }
}
