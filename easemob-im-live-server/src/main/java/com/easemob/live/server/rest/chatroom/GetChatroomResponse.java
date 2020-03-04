package com.easemob.live.server.rest.chatroom;

import com.easemob.live.server.liveroom.LiveRoomInfo;
import com.easemob.live.server.rest.RestResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetChatroomResponse extends RestResponse {

    private Object entities;

    private List<LiveRoomInfo> data;
}
