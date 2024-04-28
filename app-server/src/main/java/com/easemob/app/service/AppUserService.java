package com.easemob.app.service;

import com.easemob.app.model.*;
import org.springframework.web.multipart.MultipartFile;

public interface AppUserService {

    /**
     * 用户根据手机号、验证码登录（对应给随机字符串作为环信用户名的 IM App Demo 使用）
     *
     * @param appKey appKey
     * @param appUser appUser
     * @return UserLoginResponse
     */
    UserLoginResponse loginWithPhoneNumber(String appKey, LoginAppUser appUser);

    /**
     * 上传用户头像
     *
     * @param appkey appkey
     * @param phoneNumber phoneNumber
     * @param file file
     * @return 头像 url
     */
    String uploadAvatar(String appkey, String phoneNumber, MultipartFile file);

}
