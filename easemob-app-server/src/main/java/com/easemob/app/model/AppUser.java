package com.easemob.app.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AppUser {
    @NotEmpty(message = "userAccount cannot be empty")
    private String userAccount;

    private String userPassword;

    private String userNickname;
}
