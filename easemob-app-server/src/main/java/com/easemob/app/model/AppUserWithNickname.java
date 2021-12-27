package com.easemob.app.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AppUserWithNickname {
    @NotEmpty(message = "userAccount cannot be empty")
    private String userAccount;

    @NotEmpty(message = "userNickname cannot be empty")
    private String userNickname;

}
