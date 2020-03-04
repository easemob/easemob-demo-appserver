package com.easemob.live.server.rest.user;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author shenchong@easemob.com 2020/2/24
 */
public enum UserStatus {

    @JsonProperty("offline")
    OFFLINE,

    @JsonProperty("online")
    ONLINE
}
