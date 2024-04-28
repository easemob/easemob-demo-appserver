package com.easemob.app.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginAppUser {

    @NotEmpty(message = "Phone number cannot be empty.")
    private String phoneNumber;

    @NotEmpty(message = "Sms code cannot be empty.")
    private String smsCode;

}
