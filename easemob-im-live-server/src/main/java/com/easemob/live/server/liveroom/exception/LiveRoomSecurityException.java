package com.easemob.live.server.liveroom.exception;

import com.easemob.live.server.liveroom.model.AuthErrorInfo;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
public class LiveRoomSecurityException extends LiveRoomException {

    private String type;
    private AuthErrorInfo errorInfo;

    public LiveRoomSecurityException(String type, String message) {
        super(message);
        this.type = type;
    }

    public LiveRoomSecurityException(AuthErrorInfo errorInfo) {
        super(errorInfo.getMessage());
        this.errorInfo = errorInfo;
        this.type = errorInfo.getType();
    }

    public String getType() {
        return this.type;
    }

    public AuthErrorInfo getErrorInfo() {
        return this.errorInfo;
    }
}
