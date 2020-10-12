package com.easemob.live.server.liveroom.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize()
public class LiveRoomListResponse {

    private List<LiveRoomInfo> entities;

    private Integer count;

    private String cursor;
}
