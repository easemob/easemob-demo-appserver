package com.easemob.app.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class GetSmsCodeResponse {

    /**
     * 错误信息
     */
    @JsonProperty("error")
    private String error;

    /**
     * 状态
     */
    @JsonProperty("status")
    private String status;

    /**
     * 数据
     */
    @JsonProperty("data")
    private String data;

    /**
     * 错误描述
     */
    @JsonProperty("error_description")
    private String errorDescription;

    @JsonCreator
    public GetSmsCodeResponse(@JsonProperty("error") String error, @JsonProperty("status") String status,
            @JsonProperty("data") String data,
            @JsonProperty("error_description") String errorDescription) {
        this.error = error;
        this.status = status;
        this.data = data;
        this.errorDescription = errorDescription;
    }
}
