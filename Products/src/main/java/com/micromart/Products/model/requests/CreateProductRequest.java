package com.micromart.products.model.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;
    @NotNull(message = "Stock count is required")
    @PositiveOrZero(message = "Stock cannot be negative")
    private Integer stockQuantity;
    @NotNull(message = "Category ID is required")
    private String categoryId;
    private String categoryName;
    private String description;
    private String imageUrl;
    private String skuCode;
}
