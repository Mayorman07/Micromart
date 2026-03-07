package com.micromart.Payment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto implements Serializable {

    private String orderId;        // e.g., "ORD-12345"
    private String userId;         // To link back to the user
    private String userEmail;      // 💡 Required for Stripe Metadata and Receipts
    private BigDecimal totalAmount; // The exact price to charge
    private String currency;       // e.g., "USD" or "NGN"

    // Optional: Useful if you want Stripe to list specific items on the checkout page
    private List<OrderItemDto> items;

    private String status;         // PENDING, PAID, CANCELLED
}