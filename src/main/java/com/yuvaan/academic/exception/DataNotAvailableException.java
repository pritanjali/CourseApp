package com.yuvaan.academic.exception;

public class DataNotAvailableException extends Exception {

    public DataNotAvailableException(final String message) {
        super(message);
    }

    public DataNotAvailableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
