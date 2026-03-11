package com.micromart.Order.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleOrderNotFoundException(OrderNotFoundException ex, HttpServletRequest request) {
        logger.error("OrderNotFoundException: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderCancellationException.class)
    public ResponseEntity<ErrorDetails> handleOrderCancellationException(OrderCancellationException ex, HttpServletRequest request) {
        logger.error("OrderCancellationException: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("State Conflict")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.error("Validation failed: {}", errors);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message("Invalid input: " + errors.values().toString())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled Exception: ", ex);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred on the server.")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}