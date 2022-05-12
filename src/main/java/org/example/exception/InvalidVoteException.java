package org.example.exception;

public class InvalidVoteException extends Exception {
    public InvalidVoteException() {
    }

    public InvalidVoteException(String message) {
        super(message);
    }

    public InvalidVoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidVoteException(Throwable cause) {
        super(cause);
    }

    public InvalidVoteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
