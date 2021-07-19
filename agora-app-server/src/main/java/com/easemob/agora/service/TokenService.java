package com.easemob.agora.service;

import com.easemob.agora.model.TokenInfo;

/**
 * @author skyfour
 * @date 2021/2/1
 * @email skyzhang@easemob.com
 */
public interface TokenService {
    /**
     * 获取USER权限的token
     * @param userAccount 用户账号
     * @return
     */
    TokenInfo getUserToken(String userAccount);
}
