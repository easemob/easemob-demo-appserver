package com.easemob.live.server.liveroom.exception;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
public class ForbiddenOpException extends LiveRoomException {

    public ForbiddenOpException() {
        super();
    }

    public ForbiddenOpException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenOpException(String message) {
        super(message);
    }

    public ForbiddenOpException(Throwable cause) {
        super(cause);
    }
}
