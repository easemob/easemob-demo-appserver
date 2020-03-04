package com.easemob.live.server.liveroom.exception;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
public class LiveRoomException extends RuntimeException {

    public LiveRoomException() {
    }

    public LiveRoomException(String message) {
        super(message);
    }

    public LiveRoomException(String message, Throwable cause) {
        super(message, cause);
    }

    public LiveRoomException(Throwable cause) {
        super(cause);
    }

    public LiveRoomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
