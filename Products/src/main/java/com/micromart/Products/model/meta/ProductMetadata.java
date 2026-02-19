package com.micromart.products.model.meta;

import com.micromart.products.entity.Category;

import java.math.BigDecimal;

public class ProductMetadata {
    private String name;
    private BigDecimal price;
    private String category;
    private String skuCode;

    public ProductMetadata() {
    }

    public ProductMetadata(String name, BigDecimal price, String category,String skuCode ) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.skuCode = skuCode;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}