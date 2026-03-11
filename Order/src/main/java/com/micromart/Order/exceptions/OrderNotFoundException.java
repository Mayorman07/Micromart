package com.micromart.Order.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderNumber) {
        super("Order not found: " + orderNumber);
    }
}