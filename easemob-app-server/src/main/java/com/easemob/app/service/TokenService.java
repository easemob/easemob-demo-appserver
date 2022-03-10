package com.easemob.app.service;

import com.easemob.app.model.TokenInfo;

/**
 * @author skyfour
 * @date 2021/2/1
 * @email skyzhang@easemob.com
 */
public interface TokenService {

    /**
     * 获取APP权限的token
     * @return
     */
    TokenInfo getAppToken();

    /**
     * 根据用户账号获取USER权限的token
     * @param userAccount 用户账号
     * @return
     */
    TokenInfo getUserTokenWithAccount(String userAccount);

    /**
     * 根据频道名称与agoraUid获取声网rtc token
     * @param channelName 频道名称
     * @param agoraUid 声网uid
     * @return
     */
    TokenInfo getRtcToken(String channelName, Integer agoraUid);
}
