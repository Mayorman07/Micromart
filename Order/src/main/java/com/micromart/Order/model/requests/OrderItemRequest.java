package com.micromart.Order.model.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRequest {

    @NotBlank(message = "SKU Code is required")
    private String skuCode;

    @NotBlank(message = "Product name is required")
    private String productName;

    private String imageUrl;

    @NotNull(message = "Unit price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal unitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}