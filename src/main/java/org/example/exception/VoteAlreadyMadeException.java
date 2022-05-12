package org.example.exception;

public class VoteAlreadyMadeException extends Exception {
    public VoteAlreadyMadeException() {
    }

    public VoteAlreadyMadeException(String message) {
        super(message);
    }

    public VoteAlreadyMadeException(String message, Throwable cause) {
        super(message, cause);
    }

    public VoteAlreadyMadeException(Throwable cause) {
        super(cause);
    }

    public VoteAlreadyMadeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
