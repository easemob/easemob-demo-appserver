package com.easemob.app.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SendSmsRequest {
    @NotEmpty(message = "Phone number cannot be empty.")
    private String phoneNumber;

    @NotEmpty(message = "Image id cannot be empty.")
    private String imageId;

    @NotEmpty(message = "Image code id cannot be empty.")
    private String imageCode;
}
