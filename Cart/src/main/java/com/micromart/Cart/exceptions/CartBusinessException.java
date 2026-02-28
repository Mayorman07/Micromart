package com.micromart.Cart.exceptions;

public class CartBusinessException extends RuntimeException {
    public CartBusinessException(String message) {
        super(message);
    }
}