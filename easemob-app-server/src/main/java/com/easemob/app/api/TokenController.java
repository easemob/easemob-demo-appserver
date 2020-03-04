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

        redisService.saveAgoraChannelInfo(false, channelName, String.valueOf(agoraUid));
        redisService.saveUidMapper(String.valueOf(agoraUid), userAccount);

        ResponseParam responseParam = new ResponseParam();
        if (StringUtils.isNotBlank(channelName) && agoraUid != null) {
            TokenInfo token = tokenService.getRtcToken(channelName, agoraUid);
            responseParam.setAccessToken(token.getToken());
            responseParam.setExpireTimestamp(token.getExpireTimestamp());

            return ResponseEntity.ok(responseParam);
        } else {
            responseParam.setCode(ResCode.RES_REQUEST_PARAM_ERROR.code);
            responseParam.setErrorInfo("channelName or agoraUid is not null");

            return ResponseEntity.badRequest().body(responseParam);
        }
    }
}
