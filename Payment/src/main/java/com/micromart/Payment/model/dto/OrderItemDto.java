package com.micromart.Payment.model.dto;

import java.math.BigDecimal;

public class OrderItemDto {

    private String skuCode;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;

    private String imageUrl;

    // Default constructor
    public OrderItemDto() {
    }

    // All-args constructor
    public OrderItemDto(String skuCode, String productName, BigDecimal unitPrice, Integer quantity,String imageUrl) {
        this.skuCode = skuCode;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.imageUrl=imageUrl;
    }

    // Builder pattern
    public static OrderItemDtoBuilder builder() {
        return new OrderItemDtoBuilder();
    }

    // Getters and Setters
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

    // Builder class
    public static class OrderItemDtoBuilder {
        private String skuCode;
        private String productName;
        private BigDecimal unitPrice;
        private Integer quantity;

        private String imageUrl;

        public OrderItemDtoBuilder skuCode(String skuCode) {
            this.skuCode = skuCode;
            return this;
        }

        public OrderItemDtoBuilder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public OrderItemDtoBuilder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public OrderItemDtoBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderItemDtoBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }


        public OrderItemDto build() {
            return new OrderItemDto(skuCode, productName, unitPrice, quantity,imageUrl);
        }
    }
}