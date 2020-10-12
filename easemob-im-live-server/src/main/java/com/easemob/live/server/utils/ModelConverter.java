package com.easemob.live.server.utils;

import com.easemob.live.server.liveroom.api.LiveRoomInfo;
import com.easemob.live.server.liveroom.model.LiveRoomDetails;

import java.util.Map;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
public class ModelConverter {

    public static LiveRoomInfo detailsConverterLiveRoomInfo(LiveRoomDetails liveRoomDetails) {

        return LiveRoomInfo.builder()
                .id(liveRoomDetails.getId().toString())
                .name(liveRoomDetails.getName())
                .description(liveRoomDetails.getDescription())
                .owner(liveRoomDetails.getOwner())
                .created(liveRoomDetails.getCreated())
                .cover(liveRoomDetails.getCover())
                .status(liveRoomDetails.getStatus())
                .showid(liveRoomDetails.getShowid())
                .affiliationsCount(liveRoomDetails.getAffiliationsCount())
                .ext(JsonUtils.parse(liveRoomDetails.getExt(), Map.class))
                .build();
    }
}
