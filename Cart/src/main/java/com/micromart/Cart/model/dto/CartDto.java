package com.micromart.Cart.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Long cartId;
    private String userId;
    private List<CartItemDto> items;
    private Integer itemCount;
    private BigDecimal totalAmount;
    private boolean isEmpty;
    private String message;
}