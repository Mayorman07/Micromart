package com.micromart.Order.model.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.micromart.Order.enums.CancellationReason;
import com.micromart.Order.enums.OrderStatus;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private String orderNumber;
    private String userEmail;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private CancellationReason cancellationReason;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public OrderResponse() {}

    public OrderResponse(String orderNumber, String userEmail, BigDecimal totalAmount,
                         OrderStatus orderStatus, CancellationReason cancellationReason,
                         LocalDateTime createdAt, List<OrderItemResponse> items) {
        this.orderNumber = orderNumber;
        this.userEmail = userEmail;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.cancellationReason = cancellationReason;
        this.createdAt = createdAt;
        this.items = items;
    }

    public String getOrderNumber() { return orderNumber; }
    public String getUserEmail() { return userEmail; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public CancellationReason getCancellationReason() { return cancellationReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<OrderItemResponse> getItems() { return items; }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setCancellationReason(CancellationReason cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}