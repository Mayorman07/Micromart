package com.micromart.Cart.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Standard Constructors
    public Cart() {}
    public Cart(Long id, String userId, List<CartItem> items) {
        this.id = id;
        this.userId = userId;
        this.items = items;
    }

    // === Manual Builder Pattern ===
    public static CartBuilder builder() {
        return new CartBuilder();
    }

    public static class CartBuilder {
        private String userId;
        public CartBuilder userId(String userId) { this.userId = userId; return this; }
        public Cart build() {
            Cart cart = new Cart();
            cart.setUserId(userId);
            return cart;
        }
    }

    // === Manual Getters and Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<CartItem> getItems() {
        if (items == null) items = new ArrayList<>();
        return items;
    }
    public void setItems(List<CartItem> items) { this.items = items; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // === Helper Methods for Bidirectional Sync ===
    public void addItem(CartItem item) {
        getItems().add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {
        getItems().remove(item);
        item.setCart(null);
    }
}
