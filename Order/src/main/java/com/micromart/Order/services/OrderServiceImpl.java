package com.micromart.Order.services;

import com.micromart.Order.entities.Order;
import com.micromart.Order.enums.CancellationReason;
import com.micromart.Order.enums.OrderStatus;
import com.micromart.Order.exceptions.OrderAccessDeniedException;
import com.micromart.Order.exceptions.OrderCancellationException;
import com.micromart.Order.exceptions.OrderNotFoundException;
import com.micromart.Order.mapper.OrderMapper;
import com.micromart.Order.model.OrderResponse;
import com.micromart.Order.model.requests.OrderRequest;
import com.micromart.Order.repository.OrderRepository;
import com.micromart.Order.utils.OrderUtils;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        logger.info("Initiating new order for user: {}", orderRequest.getUserEmail());

        String generatedOrderNumber = OrderUtils.generateOrderNumber();
        List<OrderLineItems> lineItems = orderMapper.mapToEntityList(orderRequest.getItems());
        BigDecimal backendCalculatedTotal = lineItems.stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order(
                generatedOrderNumber,
                orderRequest.getUserEmail(),
                backendCalculatedTotal,
                OrderStatus.PENDING,
                lineItems
        );


        Order savedOrder = orderRepository.save(order);
        logger.info("Order {} successfully created and saved to database.", savedOrder.getOrderNumber());
        return orderMapper.mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderByOrderNumber(String orderNumber, String authenticatedUserEmail) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with number: " + orderNumber));

        if (!order.getUserEmail().equals(authenticatedUserEmail)) {
            throw new OrderAccessDeniedException("You do not have access to order: " + orderNumber);
        }
        return orderMapper.mapToResponse(order);
    }

    @Override
    public Page<OrderResponse> getUserOrders(String userEmail, Pageable pageable) {
        logger.info("Fetching paginated orders for user: {}", userEmail);
        Page<Order> orderPage = orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail, pageable);
        return orderPage.map(orderMapper::mapToResponse);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String orderNumber, CancellationReason reason,String authenticatedUserEmail) {
        logger.info("Attempting to cancel order {} with reason: {}", orderNumber, reason);

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with number: " + orderNumber));

        if (!order.getUserEmail().equals(authenticatedUserEmail)) {
            logger.warn("User {} attempted to cancel order {} belonging to {}", authenticatedUserEmail, orderNumber, order.getUserEmail());
            throw new OrderAccessDeniedException("You do not have permission to access or modify order: " + orderNumber);
        }

        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new OrderCancellationException("Cannot cancel an order that has already been shipped or delivered.");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);

        Order savedOrder = orderRepository.save(order);
        logger.info("Successfully cancelled order {} due to {}", orderNumber, reason);

        return orderMapper.mapToResponse(savedOrder);
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
                .map(orderMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        logger.info("Admin query: Fetching all orders with status {}", status);
        return orderRepository.findByOrderStatus(status)
                .stream()
                .map(orderMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        logger.info("Admin query: Fetching orders between {} and {}", start, end);
        return orderRepository.findByCreatedAtBetween(start, end)
                .stream()
                .map(orderMapper::mapToResponse)
                .collect(Collectors.toList());
    }
}