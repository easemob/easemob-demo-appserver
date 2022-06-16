package com.easemob.agora.service;

import com.easemob.agora.model.TokenInfo;

/**
 * @author skyfour
 * @date 2021/2/1
 * @email skyzhang@easemob.com
 */
public interface TokenService {

    /**
     * 通过username获取token
     * @param channelName
     * @param userId
     * @return
     */
    TokenInfo getRtcToken(String channelName, String userId);

    /**
     * 通过uid获取token
     * @param channelName
     * @param uid
     * @return
     */
    TokenInfo getRtcToken(String channelName, Integer uid);
}
