package com.easemob.live.server.liveroom.model;

import lombok.Getter;

@Getter
public enum AuthErrorInfo {

    OAUTH2_UNAUTHORIZED_CLIENT("unauthorized", "Unable to authenticate (OAuth)"),
    BAD_ACCESS_TOKEN_ERROR("auth_bad_access_token", "Unable to authenticate due to corrupt access token");

    private final String type;
    private final String message;

    AuthErrorInfo(String type, String message) {
        this.type = type;
        this.message = message;
    }
}
