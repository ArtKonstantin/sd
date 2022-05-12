package org.example.exception;

public class InvalidSlaException extends Exception {
    public InvalidSlaException() {
    }

    public InvalidSlaException(String message) {
        super(message);
    }

    public InvalidSlaException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSlaException(Throwable cause) {
        super(cause);
    }

    public InvalidSlaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
