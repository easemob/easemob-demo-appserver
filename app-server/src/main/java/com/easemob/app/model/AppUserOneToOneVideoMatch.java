package com.easemob.app.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AppUserOneToOneVideoMatch {

    @NotEmpty(message = "phone number cannot be empty")
    private String phoneNumber;

    private Boolean sendCancelMatchNotify = true;

}
