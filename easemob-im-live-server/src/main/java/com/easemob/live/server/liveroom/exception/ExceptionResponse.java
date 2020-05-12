package com.easemob.live.server.liveroom.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize()
public class ExceptionResponse {

    private String error;

    private String exception;

    private final long timestamp = System.currentTimeMillis();

    public long getDuration() {
        return System.currentTimeMillis() - timestamp;
    }

    @JsonProperty("error_description")
    private String errorDescription;
}
