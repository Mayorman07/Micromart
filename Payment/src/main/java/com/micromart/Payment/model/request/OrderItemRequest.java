package com.micromart.Payment.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemRequest {
    @NotBlank(message = "SKU code is required")
    private String skuCode;

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    @DecimalMin(value = "0.01", message = "Unit price must be at least 0.01")
    private BigDecimal unitPrice;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    private String imageUrl;

    public OrderItemRequest() {}

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}