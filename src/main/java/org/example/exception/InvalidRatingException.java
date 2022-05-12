package org.example.exception;

public class InvalidRatingException extends Exception {
    public InvalidRatingException() {
    }

    public InvalidRatingException(String message) {
        super(message);
    }

    public InvalidRatingException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRatingException(Throwable cause) {
        super(cause);
    }

    public InvalidRatingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
