package com.easemob.app.model.request;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class SendSmsRequest {
    /**
     * 手机号
     */
    @NotEmpty(message = "Phone number cannot be empty.")
    private String phoneNumber;

    /**
     * 图片验证码 ID
     */
    @NotEmpty(message = "Image id cannot be empty.")
    private String imageId;

    /**
     * 图片验证码内容
     */
    @NotEmpty(message = "Image code id cannot be empty.")
    private String imageCode;
}
