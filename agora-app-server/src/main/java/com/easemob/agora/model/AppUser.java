package com.easemob.agora.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AppUser {
    @NotBlank
    private String userAccount;

    @NotBlank
    private String userPassword;
}
