package com.micromart.exceptions;

public class NotFoundException extends MicroMartException{
    public NotFoundException(String message){super(message);}
    public NotFoundException(String message, Throwable cause) {super(message, cause);}

}