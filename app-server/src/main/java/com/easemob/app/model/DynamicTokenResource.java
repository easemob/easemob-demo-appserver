package com.easemob.app.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class DynamicTokenResource {

    @JsonProperty("signature")
    private String signature;

    @JsonProperty("appkey")
    private String appkey;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("curTime")
    private Long curTime;

    @JsonProperty("ttl")
    private Long ttl;

    @JsonCreator
    public DynamicTokenResource(@JsonProperty("signature") String signature,
            @JsonProperty("appkey") String appkey,
            @JsonProperty("userId") String userId,
            @JsonProperty("curTime") Long curTime,
            @JsonProperty("ttl") Long ttl) {
        this.signature = signature;
        this.appkey = appkey;
        this.userId = userId;
        this.curTime = curTime;
        this.ttl = ttl;
    }
}
