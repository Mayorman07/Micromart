package com.micromart.products.model.responses;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private String skuCode;
    private String categoryName; // Helper for the UI
    private Long categoryId;
}
