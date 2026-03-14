package com.micromart.Order.model.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class OrderItemResponse {
    private String skuCode;
    private String productName;
    private String imageUrl;
    private BigDecimal unitPrice;
    private Integer quantity;

    public OrderItemResponse() {}

    public OrderItemResponse(String skuCode, String productName, String imageUrl, BigDecimal unitPrice, Integer quantity) {
        this.skuCode = skuCode;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getSkuCode() { return skuCode; }
    public String getProductName() { return productName; }
    public String getImageUrl() { return imageUrl; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public Integer getQuantity() { return quantity; }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
