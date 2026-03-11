package com.micromart.Order.services;

import com.micromart.Order.entities.Order;
import com.micromart.Order.enums.CancellationReason;
import com.micromart.Order.enums.OrderStatus;
import com.micromart.Order.model.OrderResponse;
import com.micromart.Order.model.requests.OrderRequest;
import com.micromart.Order.model.responses.OrderItemResponse;
import com.micromart.Order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.micromart.Order.entities.OrderLineItems;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        logger.info("Initiating new order for user: {}", orderRequest.getUserEmail());

        String generatedOrderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. Map DTO items to Entities
        List<OrderLineItems> lineItems = orderRequest.getItems().stream()
                .map(item -> OrderLineItems.builder()
                        .skuCode(item.getSkuCode())
                        .productName(item.getProductName())
                        .imageUrl(item.getImageUrl())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        // 3. Secure backend calculation (ignores frontend total to prevent tampering)
        BigDecimal backendCalculatedTotal = lineItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Build the Order Entity
        Order order = Order.builder()
                .orderNumber(generatedOrderNumber)
                .userEmail(orderRequest.getUserEmail())
                .totalAmount(backendCalculatedTotal)
                .orderStatus(OrderStatus.PENDING)
                .orderLineItemsList(lineItems)
                .build();

        Order savedOrder = orderRepository.save(order);
        logger.info("Order {} successfully created and saved to database.", savedOrder.getOrderNumber());

        return mapToOrderResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));
        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getUserOrders(String userEmail) {
        logger.info("Fetching all orders for user: {}", userEmail);
        List<Order> orders = orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrderResponse> getUserOrders(String userEmail, Pageable pageable) {
        logger.info("Fetching paginated orders for user: {}", userEmail);
        Page<Order> orderPage = orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail, pageable);

        // Page.map() cleanly converts the Page of Entities into a Page of DTOs
        return orderPage.map(this::mapToOrderResponse);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String orderNumber, CancellationReason reason) {
        logger.info("Attempting to cancel order {} with reason: {}", orderNumber, reason);

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));

        // Prevent cancelling orders that are already shipped or delivered
        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel an order that has already been shipped or delivered.");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason); // Now strictly typed to our Enum

        Order savedOrder = orderRepository.save(order);
        logger.info("Successfully cancelled order {} due to {}", orderNumber, reason);

        return mapToOrderResponse(savedOrder);
    }

    // --- Extended Query Methods ---

    @Override
    public boolean checkOrderExists(String orderNumber) {
        return orderRepository.existsByOrderNumber(orderNumber);
    }

    @Override
    public long getUserOrderCount(String userEmail) {
        return orderRepository.countByUserEmail(userEmail);
    }

    @Override
    public List<OrderResponse> getUserOrdersByStatus(String userEmail, OrderStatus status) {
        logger.info("Fetching orders for user {} with status {}", userEmail, status);
        return orderRepository.findByUserEmailAndOrderStatus(userEmail, status)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        logger.info("Admin query: Fetching all orders with status {}", status);
        return orderRepository.findByOrderStatus(status)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        logger.info("Admin query: Fetching orders between {} and {}", start, end);
        return orderRepository.findByCreatedAtBetween(start, end)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    // --- Helper Method: Map Entity back to Response DTO ---

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderLineItemsList().stream()
                .map(item -> OrderItemResponse.builder()
                        .skuCode(item.getSkuCode())
                        .productName(item.getProductName())
                        .imageUrl(item.getImageUrl())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .userEmail(order.getUserEmail())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .cancellationReason(order.getCancellationReason())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}
