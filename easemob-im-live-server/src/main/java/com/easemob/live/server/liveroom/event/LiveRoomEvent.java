package com.easemob.live.server.liveroom.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @author shenchong@easemob.com 2020/10/10
 */
@Data
public class LiveRoomEvent extends ApplicationEvent {

    private final String liveroomId;

    private final EventType type;

    public LiveRoomEvent(String liveroomId, EventType type) {
        super("LiveRoom Event");
        this.type = type;
        this.liveroomId = liveroomId;
    }
}
