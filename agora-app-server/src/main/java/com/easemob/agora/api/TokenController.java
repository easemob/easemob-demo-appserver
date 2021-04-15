package com.easemob.agora.api;

import com.alibaba.fastjson.JSONObject;
import com.easemob.agora.model.ResponseParam;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.RedisService;
import com.easemob.agora.service.TokenService;
import com.easemob.agora.utils.RandomUidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RedisService redisService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token/rtcToken")
    public ResponseParam getAgoraToken(
            @RequestParam(name = "channelName", required = false) String channelName,
            @RequestParam(name = "userAccount", required = false) String userId,
            @RequestParam(name = "agoraUserId", required = false) Integer agoraUserId,
            @RequestBody(required = false) JSONObject body) {

        String uid;
        String easemobUserId = null;
        boolean isRandomUid = false;

        if (agoraUserId == null || agoraUserId == 0) {
            isRandomUid = true;
            uid = RandomUidUtil.randomUid();
        } else {
            uid = String.valueOf(agoraUserId);
        }

        if (!StringUtils.isEmpty(userId)) {
            easemobUserId = userId;
        }

        if (body != null) {
            if (body.containsKey("agoraUserId")) {
                Integer tempAgoraUserId = body.getInteger("agoraUserId");
                if (tempAgoraUserId == null || tempAgoraUserId == 0) {
                    isRandomUid = true;
                    uid = RandomUidUtil.randomUid();
                } else {
                    uid = body.getInteger("agoraUserId").toString();
                }
            } else {
                isRandomUid = true;
                uid = RandomUidUtil.randomUid();
            }
        }

        if (StringUtils.isEmpty(easemobUserId) && body != null) {
            if (body.containsKey("username")) {
                easemobUserId = body.getString("username");
            }
        }

        if (StringUtils.isEmpty(channelName) && body != null) {
            channelName = body.getString("channelName");
        }
        ResponseParam responseParam = new ResponseParam();
        TokenInfo token = tokenService.getRtcToken(channelName, uid);
        redisService.saveAgoraChannelInfo(isRandomUid, channelName, uid);
        redisService.saveUidMapper(uid, easemobUserId);
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTime(token.getExpireTime());
        responseParam.setAgoraUserId(Integer.valueOf(uid));
        return responseParam;
    }

    @GetMapping("/token/rtcToken")
    public ResponseParam getAgoraToken(
            @RequestParam(name = "channelName", required = false) String channelName,
            @RequestParam(name = "userAccount", required = false) String userId,
            @RequestParam(name = "agoraUserId", required = false) Integer agoraUserId) {

        String uid;
        String easemobUserId = null;
        boolean isRandomUid = false;

        if (agoraUserId == null || agoraUserId == 0) {
            isRandomUid = true;
            uid = RandomUidUtil.randomUid();
        } else {
            uid = String.valueOf(agoraUserId);
        }

        if (!StringUtils.isEmpty(userId)) {
            easemobUserId = userId;
        }

        ResponseParam responseParam = new ResponseParam();
        TokenInfo token = tokenService.getRtcToken(channelName, uid);
        redisService.saveAgoraChannelInfo(isRandomUid, channelName, uid);
        redisService.saveUidMapper(uid, easemobUserId);
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTime(token.getExpireTime());
        responseParam.setAgoraUserId(Integer.valueOf(uid));
        return responseParam;
    }

    @GetMapping("/token/liveToken")
    public ResponseParam getAgoraLiveToken(
            @RequestParam(name = "channelName", required = false) String channelName,
            @RequestParam(name = "userAccount", required = false) String userId,
            @RequestParam(name = "uid", required = false, defaultValue = "0") Integer uid) {
        ResponseParam responseParam = new ResponseParam();
        TokenInfo token = tokenService.getRtcToken(channelName, uid);
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTime(token.getExpireTime());
        return responseParam;
    }

}
