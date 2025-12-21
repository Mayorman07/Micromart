package com.micromart.Users.exceptions;

public class WeakPasswordException extends MicroMartException{
    public WeakPasswordException(String message) {
        super(message);
    }
    public WeakPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
