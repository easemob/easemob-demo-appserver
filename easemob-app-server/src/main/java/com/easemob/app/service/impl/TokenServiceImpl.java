package com.easemob.app.service.impl;

import com.easemob.app.exception.ASException;
import com.easemob.app.exception.ASNotFoundException;
import com.easemob.app.model.AppUserInfo;
import com.easemob.app.model.TokenInfo;
import com.easemob.app.service.AssemblyService;
import com.easemob.app.service.RestService;
import com.easemob.app.service.TokenService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.agora.chat.ChatTokenBuilder2;
import io.agora.media.RtcTokenBuilder2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Value("${agora.token.expire.period.seconds}")
    private int expirePeriod;

    @Value("${application.agora.appId}")
    private String agoraAppId;

    @Value("${application.agora.appCert}")
    private String agoraCert;

    @Autowired
    private AssemblyService assemblyService;

    @Autowired
    private RestService restService;

    private Cache<String, TokenInfo>
            appTokenCache = CacheBuilder.newBuilder().maximumSize(1).expireAfterWrite(6, TimeUnit.HOURS).build();

    @Override
    public TokenInfo getUserTokenWithAccount(String appkey, String userAccount) {
        log.info("userAccount get user token :{}", userAccount);
        AppUserInfo appUserInfo = this.assemblyService.getAppUserInfoFromDB(appkey, userAccount);
        if (appUserInfo != null) {
            String chatUserName = appUserInfo.getChatUserName().toLowerCase();
            String chatUserUuid = this.restService.getChatUserUuid(appkey, chatUserName);
            return getTokenInfo(chatUserUuid, appUserInfo.getAgoraUid());
        } else {
            throw new ASNotFoundException(String.format("%s not exists", userAccount));
        }
    }

    @Override public TokenInfo getAppToken() {
        try {
            return appTokenCache.get("app-token", () -> {
                ChatTokenBuilder2 builder = new ChatTokenBuilder2();
                String appToken =
                        builder.buildAppToken(agoraAppId, agoraCert, expirePeriod);

                TokenInfo tokenInfo = new TokenInfo();
                tokenInfo.setToken(appToken);
                tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
                return tokenInfo;
            });
        } catch (Exception e) {
            log.error("get app token error. e : {}", e.getMessage());
            throw new ASException("get app token error.");
        }
    }

    @Override
    public TokenInfo getRtcToken(String channelName, Integer agoraUid) {
        RtcTokenBuilder2 rtcTokenBuilder = new RtcTokenBuilder2();
        String token =
                rtcTokenBuilder.buildTokenWithUid(agoraAppId, agoraCert, channelName, agoraUid,
                        RtcTokenBuilder2.Role.ROLE_PUBLISHER, this.expirePeriod, this.expirePeriod);

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(token);
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);

        return tokenInfo;
    }

    private TokenInfo getTokenInfo(String chatUserUuid, String agoraUid) {
        ChatTokenBuilder2 builder = new ChatTokenBuilder2();
        String userToken =
                builder.buildUserToken(agoraAppId, agoraCert, chatUserUuid, expirePeriod);

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(userToken);
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
        tokenInfo.setAgoraUid(agoraUid);
        return tokenInfo;
    }
}
