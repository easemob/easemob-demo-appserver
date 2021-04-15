package com.easemob.agora.service.impl;

import com.easemob.agora.AgoraIO.RtcTokenGenerate;
import com.easemob.agora.config.ApplicationConf;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author skyfour
 * @date 2021/2/2
 * @email skyzhang@easemob.com
 */
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private ApplicationConf applicationConf;

    @Value("${agora.expira.time.seconds:86400}")
    private int expireTime;

    @Override public TokenInfo getRtcToken(String channelName, String userId) {
        if (StringUtils.isEmpty(channelName) || StringUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("channelName or userId must not null.");
        }
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setExpireTime(expireTime);
        tokenInfo.setToken(RtcTokenGenerate
                .generateToken(applicationConf.getAgoraAppId(), applicationConf.getAgoraCert(),
                        channelName, userId, expireTime));
        return tokenInfo;
    }

    @Override public TokenInfo getRtcToken(String channelName, Integer uid) {
        if (StringUtils.isEmpty(channelName) || uid == null) {
            throw new IllegalArgumentException("channelName or userId must not null.");
        }
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setExpireTime(expireTime);
        tokenInfo.setToken(RtcTokenGenerate
                .generateToken(applicationConf.getAgoraAppId(), applicationConf.getAgoraCert(),
                        channelName, uid, expireTime));
        return tokenInfo;
    }
}
