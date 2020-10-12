package com.easemob.live.server.liveroom.event;

/**
 * @author shenchong@easemob.com 2020/10/10
 */
public class LiveRoomOngoingEvent extends LiveRoomEvent {

    public LiveRoomOngoingEvent(String liveroomId) {
        super(liveroomId, EventType.ONGOING);
    }
}
