package com.micromart.Order.services;

import com.micromart.Order.enums.CancellationReason;
import com.micromart.Order.model.OrderResponse;
import com.micromart.Order.model.requests.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.micromart.Order.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    OrderResponse getOrderByOrderNumber(String orderNumber, String authenticatedUserEmail);
    Page<OrderResponse> getUserOrders(String userEmail, Pageable pageable);
    OrderResponse cancelOrder(String orderNumber, CancellationReason reason,String authenticatedUserEmail);

    boolean checkOrderExists(String orderNumber);
    long getUserOrderCount(String userEmail);
    List<OrderResponse> getUserOrdersByStatus(String userEmail, OrderStatus status);
    List<OrderResponse> getOrdersByStatus(OrderStatus status);
    List<OrderResponse> getOrdersByDateRange(LocalDateTime start, LocalDateTime end);
}
