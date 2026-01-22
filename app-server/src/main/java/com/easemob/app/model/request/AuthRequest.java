package com.easemob.app.model.request;

public class AuthRequest {

    /**
     * 用户身份标识
     */
    private String identity;

    public AuthRequest() {
    }

    public AuthRequest(String identity) {
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
