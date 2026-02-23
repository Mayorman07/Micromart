package com.micromart.Cart.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

    private Long id;
    private String skuCode;
    private String productName;      // Snapshot
    private String imageUrl;         // Snapshot
    private BigDecimal unitPrice;    // Snapshot
    private Integer quantity;        // Quantity belongs HERE
    private BigDecimal subtotal;     // quantity * unitPrice
}

