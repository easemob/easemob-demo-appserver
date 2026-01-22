package com.easemob.app.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class DynamicTokenResource {

    /**
     * 签名
     */
    @JsonProperty("signature")
    private String signature;

    /**
     * 环信 AppKey
     */
    @JsonProperty("appkey")
    private String appkey;

    /**
     * 用户 ID
     */
    @JsonProperty("userId")
    private String userId;

    /**
     * 当前时间戳
     */
    @JsonProperty("curTime")
    private Long curTime;

    /**
     * 过期时间 (Time To Live)
     */
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
