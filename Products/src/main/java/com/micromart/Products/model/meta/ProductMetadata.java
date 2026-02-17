package com.micromart.products.model.meta;

import java.math.BigDecimal;

public class ProductMetadata {
    private String name;
    private BigDecimal price;
    private String category;

    public ProductMetadata() {
    }

    public ProductMetadata(String name, BigDecimal price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}