package com.easemob.app.model.request;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class LoginAppUser {

    /**
     * 手机号
     */
    @NotEmpty(message = "Phone number cannot be empty.")
    private String phoneNumber;

    /**
     * 短信验证码
     */
    @NotEmpty(message = "Sms code cannot be empty.")
    private String smsCode;

}
