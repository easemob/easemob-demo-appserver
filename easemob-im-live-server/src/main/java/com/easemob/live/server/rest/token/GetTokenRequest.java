package com.easemob.live.server.rest.token;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize()
public class GetTokenRequest {

    @JsonProperty("grant_type")
    private GrantType grantType;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    public enum GrantType {
        @JsonProperty("password")
        PASSWORD,

        @JsonProperty("client_credentials")
        CLIENT_CREDENTIALS
    }
}
