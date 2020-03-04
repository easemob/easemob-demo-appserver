package com.easemob.live.server.rest.chatroom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
@Builder
@Data
public class CreateChatroomRequest {

    private String name;

    private String description;

    @JsonProperty("maxusers")
    private Integer maxUsers;

    private String owner;

    private Boolean mute;

    private List<String> members;

    private Scale scale;

    public enum Scale {
        @JsonProperty("large")
        LARGE,
        @JsonProperty("normal")
        NORMAL
    }
}
