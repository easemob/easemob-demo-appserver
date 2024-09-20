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

    @GetMapping("/inside/token/rtc/channel/{channelName}/phoneNumber/{phoneNumber}")
    public ResponseEntity getAgoraRtcTokenWithPhoneNumber(@PathVariable String channelName,
            @PathVariable String phoneNumber) {

        ResponseParam responseParam = new ResponseParam();
        if (StringUtils.isBlank(channelName) || phoneNumber == null) {
            responseParam.setCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());
            responseParam.setErrorInfo("channelName or phoneNumber is not null.");
        }

        TokenInfo token = tokenService.getRtcToken(channelName, phoneNumber);
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTimestamp(token.getExpireTimestamp());

        return ResponseEntity.ok(responseParam);
    }
}
