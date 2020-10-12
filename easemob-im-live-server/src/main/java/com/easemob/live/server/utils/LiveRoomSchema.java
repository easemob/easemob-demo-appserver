package com.easemob.live.server.utils;

import com.easemob.live.server.liveroom.api.LiveRoomRequest;
import com.easemob.live.server.liveroom.model.LiveRoomDetails;
import org.apache.commons.lang.StringUtils;

/**
 * @author shenchong@easemob.com 2020/2/24
 */
public class LiveRoomSchema {

    public static boolean checkModifiedFieldsForChatRoom(LiveRoomRequest request) {

        return request.getName() != null ||
                request.getDescription() != null ||
                request.getMaxUsers() != null;
    }

    public static LiveRoomDetails normalizeLiveRoomDetails(LiveRoomDetails liveRoomDetails,
            LiveRoomRequest request) {

        if (StringUtils.isNotBlank(request.getName())) {
            liveRoomDetails.setName(request.getName());
        }

        if (StringUtils.isNotBlank(request.getDescription())) {
            liveRoomDetails.setDescription(request.getDescription());
        }

        if (StringUtils.isNotBlank(request.getCover())) {
            liveRoomDetails.setCover(request.getCover());
        }

        if (request.getExt() != null) {
            liveRoomDetails.setExt(JsonUtils.mapToJsonString(request.getExt()));
        }

        return liveRoomDetails;
    }
}
