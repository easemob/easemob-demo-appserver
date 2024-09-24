package com.easemob.app.service.impl;

import com.easemob.app.exception.ASNotFoundException;
import com.easemob.app.model.AppUserOneToOneVideoInfo;
import com.easemob.app.model.TokenInfo;
import com.easemob.app.service.*;
import com.easemob.app.utils.agoratools.media.AccessToken2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Value("${application.1v1.video.appkey}")
    private String videoAppKey;

    @Value("${agora.token.expire.period.seconds}")
    private int expirePeriod;

    @Value("${application.agoraAppId}")
    private String agoraAppId;

    @Value("${application.agoraCert}")
    private String agoraAppCert;

    @Autowired
    private AssemblyService assemblyService;

    @Autowired
    private RedisService redisService;

    @Override public TokenInfo getOneToOneVideoRtcToken(String channelName, String phoneNumber) {
        AppUserOneToOneVideoInfo appUserInfo = this.assemblyService.getAppUserOneToOneVideoInfoFromDB(videoAppKey, phoneNumber);
        if (appUserInfo == null) {
            throw new ASNotFoundException("phoneNumber " + phoneNumber + " does not exist.");
        } else {
            String agoraUid = appUserInfo.getAgoraUid();
            String chatUsername = appUserInfo.getChatUserName();
            TokenInfo tokenInfo = getRtcToken(channelName, Integer.valueOf(appUserInfo.getAgoraUid()));

            redisService.saveAgoraChannelInfo(false, channelName, agoraUid);
            redisService.saveUidMapper(agoraUid, chatUsername);

            return tokenInfo;
        }
    }

    @Override public TokenInfo getRtcToken(String channelName, Integer agoraUid) {
        TokenInfo tokenInfo = new TokenInfo();
        AccessToken2 accessToken2 = new AccessToken2(agoraAppId, agoraAppCert, 86400);
        AccessToken2.Service serviceRtc = new AccessToken2.ServiceRtc(channelName, String.valueOf(agoraUid));

        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_JOIN_CHANNEL, 86400);
        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_AUDIO_STREAM, 86400);
        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_VIDEO_STREAM, 86400);
        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_DATA_STREAM, 86400);
        accessToken2.addService(serviceRtc);

        String token;
        try {
            token = accessToken2.build();
        } catch (Exception e) {
            token = "";
            log.error("generate agora rtc token error. e : {}", e.getMessage());
        }

        tokenInfo.setToken(token);
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
        tokenInfo.setAgoraUid(String.valueOf(agoraUid));
        return tokenInfo;
    }

}
