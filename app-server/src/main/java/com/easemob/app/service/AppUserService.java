package com.easemob.app.service;

import com.easemob.app.model.*;
import org.springframework.web.multipart.MultipartFile;

public interface AppUserService {

    /**
     * 用户根据手机号、验证码登录（给1v1视频通话demo使用）
     *
     * @param appKey appKey
     * @param appUser appUser
     * @return UserLoginResponse
     */
    UserLoginResponse oneToOneVideoLogin(String appKey, LoginAppUser appUser);

    /**
     * 1v1视频上传用户头像
     *
     * @param appkey appkey
     * @param phoneNumber phoneNumber
     * @param file file
     * @return 头像 url
     */
    String oneToOneVideoUploadAvatar(String appkey, String phoneNumber, MultipartFile file);

}
