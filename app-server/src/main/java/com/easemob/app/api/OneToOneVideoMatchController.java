package com.easemob.app.api;

import com.easemob.app.exception.ASUnAuthorizedException;
import com.easemob.app.model.AppUserOneToOneVideoMatch;
import com.easemob.app.model.OneToOneVideoMatchInfo;
import com.easemob.app.model.ResCode;
import com.easemob.app.model.ResponseParam;
import com.easemob.app.service.OneToOneVideoMatchService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class OneToOneVideoMatchController {

    private final static String Bearer = "Bearer ";

    @Value("${application.1v1.video.appkey}")
    private String videoAppKey;

    private OneToOneVideoMatchService oneToOneVideoMatchService;

    public OneToOneVideoMatchController(OneToOneVideoMatchService oneToOneVideoMatchService) {
        this.oneToOneVideoMatchService = oneToOneVideoMatchService;
    }

    @PostMapping("/inside/app/user/1v1/video/match")
    public ResponseEntity oneToOneVideoUserMatch(@RequestBody @Valid AppUserOneToOneVideoMatch appUserOneToOneVideoMatch,
            @RequestHeader(value = "Authorization") String token) {
        String phoneNumber = appUserOneToOneVideoMatch.getPhoneNumber();

        if (StringUtils.isEmpty(phoneNumber)) {
            throw new IllegalArgumentException("Phone number cannot be empty.");

        }

        if (StringUtils.isEmpty(token)) {
            throw new ASUnAuthorizedException("Unable to authenticate due to corrupt access token.");
        }

        if (token.startsWith(Bearer)) {
            token = token.substring(Bearer.length());
        }

        OneToOneVideoMatchInfo oneToOneVideoMatchInfo =
                oneToOneVideoMatchService.matchUser(videoAppKey, phoneNumber,
                        appUserOneToOneVideoMatch.getSendCancelMatchNotify(), token);

        ResponseParam responseParam = new ResponseParam();
        responseParam.setCode(ResCode.RES_OK.getCode());
        responseParam.setAgoraUid(oneToOneVideoMatchInfo.getAgoraUid());
        responseParam.setChannelName(oneToOneVideoMatchInfo.getChannelName());
        responseParam.setRtcToken(oneToOneVideoMatchInfo.getRtcToken());
        responseParam.setMatchedUser(oneToOneVideoMatchInfo.getMatchedUser());
        responseParam.setMatchedChatUser(oneToOneVideoMatchInfo.getMatchedChatUser());

        return ResponseEntity.ok(responseParam);
    }

    @DeleteMapping("/inside/app/user/{phoneNumber}/1v1/video/match")
    public ResponseEntity oneToOneVideoUserUnMatch(@PathVariable("phoneNumber") String phoneNumber,
            @RequestHeader(value = "Authorization") String token) {

        if (StringUtils.isEmpty(phoneNumber)) {
            throw new IllegalArgumentException("Phone number cannot be empty.");

        }

        if (StringUtils.isEmpty(token)) {
            throw new ASUnAuthorizedException("Unable to authenticate due to corrupt access token.");
        }

        if (token.startsWith(Bearer)) {
            token = token.substring(Bearer.length());
        }

        oneToOneVideoMatchService.unMatchUser(videoAppKey, phoneNumber, token);

        ResponseParam responseParam = new ResponseParam();
        return ResponseEntity.ok(responseParam);
    }

    @GetMapping("/inside/app/user/{chatUsername}/1v1/video/match/status")
    public ResponseEntity getOneToOneVideoUserMatchStatus(@PathVariable("chatUsername") String chatUsername) {
        String matchStatus = oneToOneVideoMatchService.getUserMatchStatus(videoAppKey, chatUsername);

        ResponseParam responseParam = new ResponseParam();
        responseParam.setCode(ResCode.RES_OK.getCode());
        responseParam.setMatchStatus(matchStatus);

        return ResponseEntity.ok(responseParam);
    }

}
