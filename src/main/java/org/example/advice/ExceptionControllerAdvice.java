package org.example.advice;

import org.example.dto.ExceptionDTO;
import org.example.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDTO handle(ForbiddenException e) {
        e.printStackTrace();
        return new ExceptionDTO("forbidden exception");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionDTO handle(NotAuthenticatedException e) {
        e.printStackTrace();
        return new ExceptionDTO("not authenticated");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handle(PasswordNotMatchesException e) {
        e.printStackTrace();
        return new ExceptionDTO("password not matches");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handle(TicketNotFoundException e) {
        e.printStackTrace();
        return new ExceptionDTO("ticket not found");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handle(InvalidDescriptionException e) {
        e.printStackTrace();
        return new ExceptionDTO("invalid description");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handle(InvalidSlaException e) {
        e.printStackTrace();
        return new ExceptionDTO("invalid sla");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handle(InvalidSolutionException e) {
        e.printStackTrace();
        return new ExceptionDTO("invalid solution");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handle(UserNotFoundException e) {
        e.printStackTrace();
        return new ExceptionDTO("user not found");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handle(InvalidRoleException e) {
        e.printStackTrace();
        return new ExceptionDTO("invalid role");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handle(InvalidRatingException e) {
        e.printStackTrace();
        return new ExceptionDTO("invalid rating");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handle(InvalidVoteException e) {
        e.printStackTrace();
        return new ExceptionDTO("invalid ticket id or status");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handle(VoteAlreadyMadeException e) {
        e.printStackTrace();
        return new ExceptionDTO("vote already made");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDTO handle(Exception e) {
        e.printStackTrace();
        return new ExceptionDTO("global internal error");
    }
}
