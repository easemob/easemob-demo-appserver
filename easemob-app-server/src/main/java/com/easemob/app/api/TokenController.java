package com.easemob.app.api;

import com.easemob.app.model.ResCode;
import com.easemob.app.model.ResponseParam;
import com.easemob.app.model.TokenInfo;
import com.easemob.app.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/token/chat/user/{account}")
    public ResponseEntity getAgoraChatToken(@PathVariable String account) {
        ResponseParam responseParam = new ResponseParam();
        if (StringUtils.isNotBlank(account)) {
            TokenInfo token = tokenService.getUserTokenWithAccount(account);
            responseParam.setAccessToken(token.getToken());
            responseParam.setExpireTimestamp(token.getExpireTimestamp());
            return ResponseEntity.ok(responseParam);
        } else {
            responseParam.setCode(ResCode.RES_REQUEST_PARAM_ERROR);
            responseParam.setErrorInfo("account is not null");
            return ResponseEntity.badRequest().body(responseParam);
        }
    }

    // to do 考虑server sdk提供直接生成rtc token的方法
//    @GetMapping("/token/rtc/channel/{channelName}/agorauid/{agoraUid}")
    public ResponseEntity getAgoraRtcToken(@PathVariable String channelName,
                                          @PathVariable Integer agoraUid) {
        ResponseParam responseParam = new ResponseParam();
        if (StringUtils.isNotBlank(channelName) && agoraUid != null) {
            TokenInfo token = tokenService.getRtcToken(channelName, agoraUid);
            responseParam.setAccessToken(token.getToken());
            responseParam.setExpireTimestamp(token.getExpireTimestamp());
            return ResponseEntity.ok(responseParam);
        } else {
            responseParam.setCode(ResCode.RES_REQUEST_PARAM_ERROR);
            responseParam.setErrorInfo("channelName or agoraUid is not null");
            return ResponseEntity.badRequest().body(responseParam);
        }
    }
}
