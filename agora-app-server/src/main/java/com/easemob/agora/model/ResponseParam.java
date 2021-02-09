package com.easemob.agora.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * description:
 * author: lijian
 * date: 2021-01-25
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseParam {
    private ResCode code = ResCode.RES_0K;
    private String appkey;
    private String channel;
    private String userId;
    private String accessToken;
    private String token;
    private String errorInfo;
    private Integer expireTime;
}
