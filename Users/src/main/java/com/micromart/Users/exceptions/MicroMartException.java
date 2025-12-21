package com.micromart.Users.exceptions;

public class MicroMartException extends RuntimeException{
    MicroMartException(String message){super (message); }
    MicroMartException(String message, Throwable cause){
        super(message, cause);
        if(this.getCause() == null && cause != null){
            this.initCause(cause);
        }
    }
}