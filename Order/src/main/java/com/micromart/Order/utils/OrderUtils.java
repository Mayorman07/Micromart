package com.micromart.Order.utils;

import java.util.UUID;

public class OrderUtils {

    private OrderUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Generates a unique 12-character order tracking number (e.g., ORD-A1B2C3D4)
     */
    public static String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}