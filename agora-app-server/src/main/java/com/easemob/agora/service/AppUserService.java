package com.easemob.agora.service;

import com.easemob.agora.model.AppUser;
import com.easemob.agora.model.TokenInfo;

public interface AppUserService {
    /**
     * 在应用中为用户注册一个账号
     * 此方法会为用户在按照 em前缀 + 随机数字的规则 在 chat 服务器注册一个 chatId 以及在用随机数字作为 agoraId 与用户绑定
     * 注册 chatId 的规则开发者自己可以定义，这里的规则仅作为参考
     * @param appUser appUser
     * @return 注册成功或失败
     */
    void registerUser(AppUser appUser);

    /**
     * 在应用中为用户注册一个账号（此账号是与chat username一致，是为了兼容 chat 客户端 demo，开发者无需关注此方法）
     * @param appUser appUser
     * @return 注册成功或失败
     */
    void registerWithChatUser(AppUser appUser);

    /**
     * 用户在应用上登录并获取一个token
     * 如果登录的用户没有注册过，会自动注册用户并按照 em前缀 + 随机数字的规则 在 chat 服务器注册一个 chatId 以及在用随机数字作为 agoraId 与用户绑定
     * 注册 chatId 的规则开发者自己可以定义，这里的规则仅作为参考
     * @param appUser appUser
     * @return token信息
     */
    TokenInfo loginUser(AppUser appUser);

    /**
     * 用户在应用上登录并获取一个token（此账号是与chat username一致，是为了兼容chat客户端demo，开发者无需关注此方法）
     * @param appUser appUser
     * @return token信息
     */
    TokenInfo loginWithChatUser(AppUser appUser);
}
