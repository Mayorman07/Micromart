package com.micromart.Products.model.responses;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private String skuCode;
    private String categoryName; // Helper for the UI
    private Long categoryId;
}
