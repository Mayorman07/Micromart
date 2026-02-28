package com.micromart.Cart.model.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMetadata {
    private String name;
    private BigDecimal price;
    private String category;
    private String skuCode;
    private String imageUrl;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}