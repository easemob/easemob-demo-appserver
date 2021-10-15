package com.easemob.agora.service.impl;

import com.easemob.agora.exception.ASNotFoundException;
import com.easemob.agora.model.AppUserInfo;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.AssemblyService;
import com.easemob.agora.service.ServerSDKService;
import com.easemob.agora.service.TokenService;
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

    @Override
    public TokenInfo getUserTokenWithAccount(String userAccount) {
        log.info("userAccount get user token :{}", userAccount);
        AppUserInfo appUserInfo = this.assemblyService.getAppUserInfoFromDB(userAccount);
        if(appUserInfo != null) {
            appUserInfo = this.assemblyService.checkAppUserInfo(appUserInfo);
            String chatUserName = appUserInfo.getChatUserName();
            String chatUserId = this.serverSDKService.getChatUserId(chatUserName);
            return getTokenInfo(chatUserName, chatUserId, appUserInfo.getAgoraUid());
        } else {
            throw new ASNotFoundException(String.format("%s not exists", userAccount));
        }
    }

    @Override
    public TokenInfo getRtcToken(String channelName, Integer agoraUid) {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(this.serverSDKService.generateAgoraRtcToken(channelName, agoraUid));
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
//        tokenInfo.setChatUserName(easemobUserName);
//        tokenInfo.setAgoraUid(agoraUid);
        return tokenInfo;
    }

    private TokenInfo getTokenInfo(String chatUserName, String chatUserId, String agoraUid) {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(this.serverSDKService.generateAgoraChatUserToken(chatUserName, chatUserId));
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
        tokenInfo.setChatUserName(chatUserName);
        tokenInfo.setAgoraUid(agoraUid);
        return tokenInfo;
    }
}
