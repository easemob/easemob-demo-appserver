package com.easemob.app.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseParam {
    /**
     * 响应状态码
     */
    private int code = 200;
    /**
     * 环信 AppKey
     */
    private String appkey;
    /**
     * 频道
     */
    private String channel;
    /**
     * 用户 ID
     */
    private String userId;
    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 令牌
     */
    private String token;
    /**
     * 错误信息
     */
    private String errorInfo;
    /**
     * 过期时间戳
     */
    private Long expireTimestamp;
    /**
     * 环信用户名
     */
    private String chatUserName;
    /**
     * 环信用户昵称
     */
    private String chatUserNickname;
    /**
     * 声网 UID
     */
    private String agoraUid;
    /**
     * 扩展数据
     */
    private Map<String, String> data;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 头像 URL
     */
    private String avatarUrl;
}
