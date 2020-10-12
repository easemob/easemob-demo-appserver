package com.easemob.live.server.liveroom.event;

/**
 * @author shenchong@easemob.com 2020/10/10
 */
public class LiveRoomOfflineEvent extends LiveRoomEvent {

    public LiveRoomOfflineEvent(String liveroomId) {
        super(liveroomId, EventType.OFFLINE);
    }
}
