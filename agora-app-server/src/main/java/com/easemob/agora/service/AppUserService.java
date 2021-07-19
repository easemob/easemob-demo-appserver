package com.easemob.agora.service;

import com.easemob.agora.model.AppUser;
import com.easemob.agora.model.TokenInfo;

public interface AppUserService {
    /**
     * 在应用中为用户注册一个账号
     * @param appUser appUser
     * @return 注册成功或失败
     */
    boolean registerUser(AppUser appUser);

    /**
     * 用户在应用上登录并获取一个token
     * @param appUser appUser
     * @return token信息
     */
    TokenInfo loginUser(AppUser appUser);
}
