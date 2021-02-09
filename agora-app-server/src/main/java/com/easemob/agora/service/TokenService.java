package com.easemob.agora.service;

import com.easemob.agora.model.TokenInfo;

/**
 * @author skyfour
 * @date 2021/2/1
 * @email skyzhang@easemob.com
 */
public interface TokenService {

    TokenInfo getRtcToken(String channelName, String userId);

}
