package com.easemob.app.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class UserLoginResponse {
    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 用户名
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userName;

    /**
     * 头像 URL
     */
    private String avatarUrl;

    /**
     * 登录令牌
     */
    private String token;
}
