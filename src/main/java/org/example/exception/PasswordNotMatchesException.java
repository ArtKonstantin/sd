package org.example.exception;

public class PasswordNotMatchesException extends Exception {
    public PasswordNotMatchesException() {
    }

    public PasswordNotMatchesException(String message) {
        super(message);
    }

    public PasswordNotMatchesException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordNotMatchesException(Throwable cause) {
        super(cause);
    }

    public PasswordNotMatchesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
