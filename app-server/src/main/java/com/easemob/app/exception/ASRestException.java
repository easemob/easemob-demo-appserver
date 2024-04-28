package com.easemob.app.exception;

public class ASRestException extends RuntimeException {
    public ASRestException(String message) {
        super(message);
    }

    public ASRestException(String message, Throwable cause) {
        super(message, cause);
    }
}
