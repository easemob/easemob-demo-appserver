package com.easemob.app.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsCodeRequest {
    /**
     * 是否检查主机
     */
    @JsonProperty("checkHost")
    private Boolean checkHost;

    /**
     * 数据内容
     */
    @JsonProperty("data")
    private String data;

    /**
     * 主机地址
     */
    @JsonProperty("host")
    private String host;

    /**
     * 图片验证结果
     */
    @JsonProperty("imgVerifyResult")
    private Boolean imgVerifyResult;

    /**
     * 电话号码
     */
    @JsonProperty("telephone")
    private String telephone;
}
