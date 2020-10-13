package com.easemob.live.server.liveroom.api;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CreateLiveRoomRequest extends LiveRoomRequest {

    @NotBlank(message = "name must be provided")
    private String name;

    private String description = "nothing left here";

    @NotBlank(message = "owner must be provided")
    private String owner;

    private List<String> members;

    private Boolean mute;

    private Boolean persistent = true;
}
