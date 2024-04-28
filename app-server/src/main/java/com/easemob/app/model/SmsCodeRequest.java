package com.easemob.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsCodeRequest {
    @JsonProperty("checkHost")
    private Boolean checkHost;

    @JsonProperty("data")
    private String data;

    @JsonProperty("host")
    private String host;

    @JsonProperty("imgVerifyResult")
    private Boolean imgVerifyResult;

    @JsonProperty("telephone")
    private String telephone;
}
