package com.easemob.agora.service;

import com.easemob.agora.model.AppUser;
import com.easemob.agora.model.TokenInfo;

public interface AppUserService {
    /**
     * 在应用中为用户注册一个账号
     * @param appUser appUser
     * @return 注册成功或失败
     */
    void registerUser(AppUser appUser);

    /**
     * 在应用中为用户注册一个账号（此账号是与chat username一致，是为了兼容chat客户端demo，开发者无需关注此方法）
     * @param appUser appUser
     * @return 注册成功或失败
     */
    void registerWithChatUser(AppUser appUser);

    /**
     * 用户在应用上登录并获取一个token
     * @param appUser appUser
     * @return token信息
     */
    TokenInfo loginUser(AppUser appUser);
}
