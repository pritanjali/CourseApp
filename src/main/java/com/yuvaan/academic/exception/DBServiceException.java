package com.yuvaan.academic.exception;

public class DBServiceException extends Exception {
    
    public DBServiceException(final String message) {
        super(message);
    }

    public DBServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
