package com.easemob.app.service;

import com.easemob.app.model.TokenInfo;

/**
 * @author skyfour
 * @date 2021/2/1
 * @email skyzhang@easemob.com
 */
public interface TokenService {

    /**
     * 根据频道名称与agoraUid获取声网rtc token
     * @param channelName 频道名称
     * @param agoraUid 声网uid
     * @return TokenInfo
     */
    TokenInfo getRtcToken(String channelName, Integer agoraUid);

    /**
     * 根据频道名称获取声网rtc token，自动生成agoraUid
     * @param channelName 频道名称
     * @return TokenInfo
     */
    TokenInfo getRtcToken(String channelName);

    /**
     * 生成 dynamic token
     *
     * @param org
     * @param app
     * @param username
     * @return
     */
    TokenInfo getDynamicToken(String org, String app, String username, Long ttl);
}
