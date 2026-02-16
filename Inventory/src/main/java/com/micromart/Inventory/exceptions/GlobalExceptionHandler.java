package com.micromart.Inventory.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, String>> handleConflict(ObjectOptimisticLockingFailureException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Conflict Detected");
        error.put("message", "The inventory was updated by another process. Please refresh and try again.");
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
