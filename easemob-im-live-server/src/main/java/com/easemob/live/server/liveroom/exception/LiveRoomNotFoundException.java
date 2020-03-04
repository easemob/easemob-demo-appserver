package com.easemob.live.server.liveroom.exception;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
public class LiveRoomNotFoundException extends LiveRoomException {
    public LiveRoomNotFoundException() {
        super();
    }

    public LiveRoomNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LiveRoomNotFoundException(String message) {
        super(message);
    }

    public LiveRoomNotFoundException(Throwable cause) {
        super(cause);
    }
}
