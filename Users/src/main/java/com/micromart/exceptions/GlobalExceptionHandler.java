package com.micromart.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorMessage> handleConflictException(ConflictException ex) {
        ErrorMessage error = new ErrorMessage(
                new Date(),
                ex.getMessage(), // This gets "Existing user!"
                HttpStatus.CONFLICT.value()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGlobalException(Exception ex) {
        ErrorMessage error = new ErrorMessage(
                new Date(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}