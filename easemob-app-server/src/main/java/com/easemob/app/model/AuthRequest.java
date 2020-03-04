package com.easemob.app.model;

public class AuthRequest {

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
