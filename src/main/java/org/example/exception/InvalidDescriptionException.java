package org.example.exception;

public class InvalidDescriptionException extends Exception {
    public InvalidDescriptionException() {
    }

    public InvalidDescriptionException(String message) {
        super(message);
    }

    public InvalidDescriptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDescriptionException(Throwable cause) {
        super(cause);
    }

    public InvalidDescriptionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
