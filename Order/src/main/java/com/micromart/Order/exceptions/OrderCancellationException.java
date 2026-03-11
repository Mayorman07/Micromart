package com.micromart.Order.exceptions;

public class OrderCancellationException extends RuntimeException {
    public OrderCancellationException(String message) {
        super(message);
    }
}
