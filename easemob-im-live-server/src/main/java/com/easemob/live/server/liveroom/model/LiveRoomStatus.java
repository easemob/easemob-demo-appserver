package com.easemob.live.server.liveroom.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
public enum LiveRoomStatus {

    @JsonProperty("offline")
    OFFLINE,

    @JsonProperty("ongoing")
    ONGOING
}
