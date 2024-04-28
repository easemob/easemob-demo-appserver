package com.easemob.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseParam {
    private int code = 200;
    private String appkey;
    private String channel;
    private String userId;
    private String accessToken;
    private String token;
    private String errorInfo;
    private Long expireTimestamp;
    private String chatUserName;
    private String chatUserNickname;
    private String agoraUid;
    private Map<String, String> data;
    private String phoneNumber;
    private String avatarUrl;
}
