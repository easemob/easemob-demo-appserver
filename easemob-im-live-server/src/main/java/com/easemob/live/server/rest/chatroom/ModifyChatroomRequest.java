package com.easemob.live.server.rest.chatroom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author shenchong@easemob.com 2020/2/24
 */
@Builder
@Data
public class ModifyChatroomRequest {

    private String name;

    private String description;

    @JsonProperty("maxusers")
    private Integer maxUsers;
}
