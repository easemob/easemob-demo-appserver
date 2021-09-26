package com.easemob.agora.api;

import com.easemob.agora.exception.ASGetTokenReachedLimitException;
//import com.easemob.agora.limit.AppUserLimitService;
import com.easemob.agora.model.ResponseParam;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author skyfour
 * @date 2021/2/1
 * @email skyzhang@easemob.com
 */
@Slf4j
@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

//    @Autowired
//    private AppUserLimitService appUserLimitService;

//    @Value("${spring.redis.get.token.limit.count}")
//    private int getTokenLimitCount;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/token/{userAccount}/chatUserToken")
    public ResponseParam getAgoraChatToken(@PathVariable String userAccount) {
//        if (this.appUserLimitService.getTokenReachedLimit(userAccount) > getTokenLimitCount) {
//            throw new ASGetTokenReachedLimitException("get token reached limit");
//        }

        TokenInfo token = tokenService.getUserToken(userAccount);
        ResponseParam responseParam = new ResponseParam();
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTimestamp(token.getExpireTimestamp());
        responseParam.setEasemobUserName(token.getEasemobUserName());
        responseParam.setAgoraUid(token.getAgoraUid());
        return responseParam;
    }
}
