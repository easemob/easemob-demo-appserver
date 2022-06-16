package com.easemob.app.service.impl;

import com.easemob.app.exception.ASNotFoundException;
import com.easemob.app.model.AppUserInfo;
import com.easemob.app.model.TokenInfo;
import com.easemob.app.service.AssemblyService;
import com.easemob.app.service.RestService;
import com.easemob.app.service.ServerSDKService;
import com.easemob.app.service.TokenService;
import com.easemob.app.utils.agoratools.chat.ChatTokenBuilder2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Autowired
    private ServerSDKService serverSDKService;

    @Autowired
    private AssemblyService assemblyService;

    @Autowired
    private RestService restService;

    @Override
    public TokenInfo getUserTokenWithAccount(String appkey, String userAccount) {
        log.info("userAccount get user token :{}", userAccount);
        AppUserInfo appUserInfo = this.assemblyService.getAppUserInfoFromDB(appkey, userAccount);
        if(appUserInfo != null) {
            String chatUserName = appUserInfo.getChatUserName();
            String chatUserId = this.serverSDKService.getChatUserId(chatUserName);
            return getTokenInfo(chatUserName, chatUserId, appUserInfo.getAgoraUid());
        } else {
            throw new ASNotFoundException(String.format("%s not exists", userAccount));
        }
    }

    @Override
    public TokenInfo getUserTokenWithAccountV1(String appkey, String userAccount, String appId, String appCert) {
        log.info("userAccount get user token :{}", userAccount);
        AppUserInfo appUserInfo = this.assemblyService.getAppUserInfoFromDB(appkey, userAccount);
        if(appUserInfo != null) {
            String chatUserName = appUserInfo.getChatUserName();
            String chatUserId = this.restService.getChatUserUuid(appkey, chatUserName);
            return getTokenInfoV1(chatUserName, chatUserId, appUserInfo.getAgoraUid(), appId, appCert);
        } else {
            throw new ASNotFoundException(String.format("%s not exists", userAccount));
        }
    }

    @Override
    public TokenInfo getRtcToken(String channelName, Integer agoraUid) {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(this.serverSDKService.generateAgoraRtcToken(channelName, agoraUid));
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
        return tokenInfo;
    }

    private TokenInfo getTokenInfo(String chatUserName, String chatUserId, String agoraUid) {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(this.serverSDKService.generateAgoraChatUserToken(chatUserName, chatUserId));
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
        tokenInfo.setAgoraUid(agoraUid);
        return tokenInfo;
    }

    private TokenInfo getTokenInfoV1(String chatUserName, String chatUserUuid, String agoraUid, String appId, String appCert) {
        ChatTokenBuilder2 builder = new ChatTokenBuilder2();
        String userToken = builder.buildUserToken(appId, appCert, chatUserUuid, expirePeriod);

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(userToken);
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
        tokenInfo.setAgoraUid(agoraUid);
        return tokenInfo;
    }
}
