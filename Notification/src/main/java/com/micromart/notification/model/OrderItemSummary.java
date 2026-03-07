package com.micromart.notification.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemSummary {
    private String name;
    private BigDecimal quantity;
    private Double price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
