package com.easemob.app.service.impl;

import com.easemob.app.model.TokenInfo;
import com.easemob.app.service.*;
import com.easemob.app.utils.DynamicTokenGenerator;
import com.easemob.app.utils.RandomUidUtils;
import com.easemob.app.utils.agoratools.media.AccessToken2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;

/**
 * @author skyfour
 * @date 2021/2/2
 * @email skyzhang@easemob.com
 */
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Value("${agora.token.expire.period.seconds}")
    private int expirePeriod;

    @Value("${application.agoraAppId}")
    private String agoraAppId;

    @Value("${application.agoraCert}")
    private String agoraAppCert;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RestService restService;

    @Override
    public TokenInfo getRtcToken(String channelName, Integer agoraUid) {
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
        return tokenInfo;
    }

    @Override public TokenInfo getRtcToken(String channelName) {
        String agoraUid;
        while (true) {
            agoraUid = RandomUidUtils.getUid();
            Boolean result = redisService.checkIfUidExists(agoraUid);
            if (!result) {
                redisService.saveUid(agoraUid);
                break;
            }
        }

        TokenInfo tokenInfo = new TokenInfo();
        AccessToken2 accessToken2 = new AccessToken2(agoraAppId, agoraAppCert, 86400);
        AccessToken2.Service serviceRtc = new AccessToken2.ServiceRtc(channelName, agoraUid);

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
        tokenInfo.setAgoraUid(agoraUid);
        return tokenInfo;
    }

    @Override
    public TokenInfo getDynamicToken(String org, String app, String username, Long ttl) {
        String appkey = String.format("%s#%s", org, app);
        Tuple2<String, String> appSecret = restService.getAppSecret(appkey);
        if (appSecret == null) {
            return null;
        }
        String clientId = appSecret.getT1();
        String clientSecret = appSecret.getT2();
        Tuple2<String, Long> generate =
                DynamicTokenGenerator.generate(appkey, clientId, clientSecret, username, ttl);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(generate.getT1());
        tokenInfo.setExpireTimestamp(generate.getT2() * 1000 + ttl * 1000);
        return tokenInfo;
    }

}
