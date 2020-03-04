package com.easemob.live.server.rest.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
@Data
public class GetTokenResponse{

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    private String error;
    @JsonProperty("error_description")
    private String errorDesc;
}
