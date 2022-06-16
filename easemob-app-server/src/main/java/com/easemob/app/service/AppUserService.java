package com.easemob.app.service;

import com.easemob.app.model.AppUser;
import com.easemob.app.model.TokenInfo;

public interface AppUserService {

    /**
     * 在应用中为用户注册一个账号（此账号是与chat username一致，是为了兼容 chat 客户端 demo，开发者无需关注此方法）
     * @param appUser appUser
     * @return 注册成功或失败
     */
    void registerWithChatUser(AppUser appUser);

    /**
     * 用户在应用上登录并获取一个token（此账号是与chat username一致，是为了兼容chat客户端demo，开发者无需关注此方法）
     * @param appUser appUser 用户名与密码
     * @return token信息
     */
    TokenInfo loginWithChatUser(AppUser appUser);


    /**
     * 在应用中为用户注册一个账号（此账号是与chat username一致，是为了兼容 chat 客户端 demo，开发者无需关注此方法）
     * @param appkey appkey
     * @param appUser appUser
     * @return 注册成功或失败
     */
    void registerWithChatUserV1(String appkey, AppUser appUser);

    /**
     * 用户在应用上登录并获取一个token（此账号是与chat username一致，是为了兼容chat客户端demo，开发者无需关注此方法）
     * @param appkey appkey
     * @param appUser appUser
     * @return token信息
     */
    TokenInfo loginWithChatUserV2(String appkey, AppUser appUser, String appId, String appCert);
}
