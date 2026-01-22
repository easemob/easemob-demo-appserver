package com.easemob.app.model.response;

public class AuthResponse {

    /**
     * 认证令牌
     */
    private String authToken;

    public AuthResponse() {
    }

    public AuthResponse(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
