package com.easemob.app.service;

import com.easemob.app.model.TokenInfo;

public interface TokenService {

    /**
     * 根据频道名称与手机号获取声网rtc token
     * @param channelName 频道名称
     * @param phoneNumber 手机号
     * @return TokenInfo
     */
    TokenInfo getRtcToken(String channelName, String phoneNumber);

    /**
     * 根据频道名称与agoraUid获取声网rtc token
     * @param channelName 频道名称
     * @param agoraUid 声网uid
     * @return TokenInfo
     */
    TokenInfo getRtcToken(String channelName, Integer agoraUid);

}
