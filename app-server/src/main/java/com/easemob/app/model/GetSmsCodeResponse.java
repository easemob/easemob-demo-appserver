package com.easemob.app.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class GetSmsCodeResponse {

    @JsonProperty("error")
    private String error;

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private String data;

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
