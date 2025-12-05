package com.cargoflow.tms.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomExceptions.ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(CustomExceptions.ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(CustomExceptions.InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusTransition(CustomExceptions.InvalidStatusTransitionException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(CustomExceptions.InsufficientCapacityException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientCapacity(CustomExceptions.InsufficientCapacityException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(CustomExceptions.LoadAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse> handleLoadAlreadyBooked(CustomExceptions.LoadAlreadyBookedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }
}
