package com.micromart.Payment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    private String skuCode;          // Stock Keeping Unit (Critical for Inventory Service)
    private String productName;  // Display name (Shown on Stripe Checkout)
    private BigDecimal unitPrice; // Price per single item
    private Integer quantity;    // Number of items purchased
    private String imageUrl;
}