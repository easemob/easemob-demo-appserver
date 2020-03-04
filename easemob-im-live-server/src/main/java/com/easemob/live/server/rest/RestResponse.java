package com.easemob.live.server.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URI;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
@Data
public class RestResponse {
    private String action;
    private String application;
    private URI uri;
    private Long timestamp;
    private Long duration;
    private String organization;
    private String applicationName;
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
    private String exception;
}
