package com.easemob.live.server.liveroom.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
public enum VideoType {

    /**
     * 直播
     */
    @JsonProperty("live")
    LIVE,

    /**
     * 点播
     */
    @JsonProperty("vod")
    VOD
}
