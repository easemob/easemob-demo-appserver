package com.easemob.app.service;

import com.easemob.app.model.TokenInfo;

public interface TokenService {

    /**
     * 根据用户账号获取 USER 权限的 token
     *
     * @param appkey      appkey
     * @param userAccount 用户账号
     * @return TokenInfo
     */
    TokenInfo getUserTokenWithAccount(String appkey, String userAccount);

    /**
     * 获取 app 权限的 token
     *
     * @return TokenInfo
     */
    TokenInfo getAppToken();

    /**
     * 根据频道名称与 agoraUid 获取声网 rtc token
     *
     * @param channelName 频道名称
     * @param agoraUid    声网uid
     * @return TokenInfo
     */
    TokenInfo getRtcToken(String channelName, Integer agoraUid);
}
