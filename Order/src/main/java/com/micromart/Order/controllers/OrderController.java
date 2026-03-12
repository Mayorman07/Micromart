package com.micromart.Order.controllers;

import com.micromart.Order.enums.CancellationReason;
import com.micromart.Order.model.OrderResponse;
import com.micromart.Order.model.requests.OrderRequest;
import com.micromart.Order.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import com.micromart.Order.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal String userEmail) {

        logger.info("REST request to create order for securely authenticated user: {}", userEmail);
        orderRequest.setUserEmail(userEmail);

        OrderResponse createdOrder = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED); // Returns 201 Created
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber,
                                                               @AuthenticationPrincipal String userEmail) {
        logger.info("REST request to get order: {} for user: {}", orderNumber,userEmail);
        return ResponseEntity.ok(orderService.getOrderByOrderNumber(orderNumber,userEmail));
    }

    @GetMapping("/my-orders/paginated")
    public ResponseEntity<Page<OrderResponse>> getMyOrdersPaginated(
            Pageable pageable,
            @AuthenticationPrincipal String userEmail) {

        logger.info("REST request to get paginated orders for user: {}", userEmail);
        return ResponseEntity.ok(orderService.getUserOrders(userEmail, pageable));
    }

    @GetMapping("/my-orders/count")
    public ResponseEntity<Long> getMyOrderCount(@AuthenticationPrincipal String userEmail) {
        logger.info("REST request to get order count for user: {}", userEmail);
        return ResponseEntity.ok(orderService.getUserOrderCount(userEmail));
    }

    @PutMapping("/{orderNumber}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable String orderNumber,
            @RequestParam CancellationReason reason,
            @AuthenticationPrincipal String userEmail) {

        logger.info("REST request from {} to cancel order: {} with reason: {}",orderNumber, reason,userEmail);
        return ResponseEntity.ok(orderService.cancelOrder(orderNumber, reason,userEmail));
    }

    // ==========================================
    // ADMIN ENDPOINTS
    // ==========================================

    @GetMapping("/admin/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@RequestParam OrderStatus status) {
        logger.info("Admin request to get all orders by status: {}", status);
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/admin/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        logger.info("Admin request to get orders between {} and {}", start, end);
        return ResponseEntity.ok(orderService.getOrdersByDateRange(start, end));
    }
}
