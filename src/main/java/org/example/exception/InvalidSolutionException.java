package org.example.exception;

public class InvalidSolutionException extends Exception {
    public InvalidSolutionException() {
    }

    public InvalidSolutionException(String message) {
        super(message);
    }

    public InvalidSolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSolutionException(Throwable cause) {
        super(cause);
    }

    public InvalidSolutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
