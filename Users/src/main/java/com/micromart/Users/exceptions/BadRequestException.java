package com.micromart.Users.exceptions;

public class BadRequestException extends MicroMartException{
    public BadRequestException(String message){super(message);}
    public BadRequestException(String message, Throwable cause) {super(message, cause);}

}

