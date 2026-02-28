package com.micromart.Cart.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(nullable = false)
    private String skuCode;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private String productName;

    private String imageUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Standard Constructors
    public CartItem() {}

    // === Manual Builder Pattern ===
    public static CartItemBuilder builder() {
        return new CartItemBuilder();
    }

    public static class CartItemBuilder {
        private String skuCode;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String productName;
        private String imageUrl;

        public CartItemBuilder skuCode(String s) { this.skuCode = s; return this; }
        public CartItemBuilder quantity(Integer q) { this.quantity = q; return this; }
        public CartItemBuilder unitPrice(BigDecimal p) { this.unitPrice = p; return this; }
        public CartItemBuilder productName(String n) { this.productName = n; return this; }
        public CartItemBuilder imageUrl(String i) { this.imageUrl = i; return this; }

        public CartItem build() {
            CartItem item = new CartItem();
            item.setSkuCode(this.skuCode);
            item.setQuantity(this.quantity);
            item.setUnitPrice(this.unitPrice);
            item.setProductName(this.productName);
            item.setImageUrl(this.imageUrl);
            return item;
        }
    }

    // === Manual Getters and Setters ===
    public void setCart(Cart cart) { this.cart = cart; }
    public Cart getCart() { return cart; }

    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
