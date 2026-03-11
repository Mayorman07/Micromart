package com.micromart.Order.model;

import com.micromart.Order.enums.CancellationReason;
import com.micromart.Order.enums.OrderStatus;
import com.micromart.Order.model.responses.OrderItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private String orderNumber;
    private String userEmail;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private CancellationReason cancellationReason;
}