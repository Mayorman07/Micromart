package com.micromart.exceptions;

public class ExpiredTokenException extends MicroMartException{
    public ExpiredTokenException(String message) {
        super(message);
    }
    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

