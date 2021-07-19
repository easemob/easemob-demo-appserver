package com.easemob.agora.service;

public interface RedisService {
    /**
     * 每个用户获取token的计数，用于限流
     * @param userAccount 用户账号
     * @return
     */
    Long appUserGetCountOfToken(String userAccount);
}