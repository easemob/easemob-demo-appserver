package com.easemob.agora.api;

import com.alibaba.fastjson.JSONObject;
import com.easemob.agora.model.ResponseParam;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author skyfour
 * @date 2021/2/1
 * @email skyzhang@easemob.com
 */
@Slf4j
@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token/rtcToken")
    public ResponseParam getAgoraToken(
            @RequestParam(name = "channelName", required = false) String channelName,
            @RequestParam(name = "userAccount", required = false) String userId,
            @RequestBody(required = false) JSONObject body) {
        if (StringUtils.isEmpty(userId) && body != null) {
            userId = body.getString("username");
        }
        if (StringUtils.isEmpty(channelName) && body != null) {
            channelName = body.getString("channelName");
        }
        ResponseParam responseParam = new ResponseParam();
        TokenInfo token = tokenService.getRtcToken(channelName, userId);
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTime(token.getExpireTime());
        return responseParam;
    }

    @GetMapping("/token/rtcToken")
    public ResponseParam getAgoraToken(
            @RequestParam(name = "channelName", required = false) String channelName,
            @RequestParam(name = "userAccount", required = false) String userId) {

        ResponseParam responseParam = new ResponseParam();
        TokenInfo token = tokenService.getRtcToken(channelName, userId);
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTime(token.getExpireTime());
        return responseParam;
    }

}
