package com.easemob.live.server.liveroom.api;

import com.easemob.live.server.liveroom.model.LiveRoomStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize()
public class LiveRoomInfo {

    private String id;

    private String name;

    private String description;

    private String owner;

    private Long created;

    private Boolean mute;

    private String cover;

    private LiveRoomStatus status = LiveRoomStatus.OFFLINE;

    private long showid = 0;

    @JsonProperty("maxusers")
    private Integer maxUsers;

    @JsonProperty("affiliations_count")
    private Integer affiliationsCount;

    private Map<String, Object> ext;

    @JsonProperty("affiliations")
    private List<Map<String, Object>> affiliations;
}
