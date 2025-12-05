package com.cargoflow.tms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CustomExceptions {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidStatusTransitionException extends RuntimeException {
        public InvalidStatusTransitionException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InsufficientCapacityException extends RuntimeException {
        public InsufficientCapacityException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class LoadAlreadyBookedException extends RuntimeException {
        public LoadAlreadyBookedException(String message) {
            super(message);
        }
    }
}
