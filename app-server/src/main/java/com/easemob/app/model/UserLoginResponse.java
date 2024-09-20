package com.easemob.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class UserLoginResponse {
    private String phoneNumber;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userName;

    private String avatarUrl;

    private String token;

    private String agoraUid;

    private String roomId;
}
