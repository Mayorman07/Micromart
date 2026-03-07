package com.micromart.Payment.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    private String skuCode;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
}