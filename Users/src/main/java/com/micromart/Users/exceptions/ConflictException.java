package com.micromart.Users.exceptions;

public class ConflictException extends MicroMartException {

    public ConflictException(String message) {
        super(message);
    }
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

