package com.micromart.Order.service;

import com.micromart.Order.entities.Order;
import com.micromart.Order.entities.OrderLineItems;
import com.micromart.Order.enums.CancellationReason;
import com.micromart.Order.enums.OrderStatus;
import com.micromart.Order.exceptions.OrderAccessDeniedException;
import com.micromart.Order.exceptions.OrderCancellationException;
import com.micromart.Order.exceptions.OrderNotFoundException;
import com.micromart.Order.mapper.OrderMapper;
import com.micromart.Order.model.requests.OrderRequest;
import com.micromart.Order.model.responses.OrderResponse;
import com.micromart.Order.repository.OrderRepository;
import com.micromart.Order.services.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest orderRequest;
    private Order mockOrder;
    private OrderLineItems lineItem;
    private OrderResponse orderResponse;

    private final String AUTH_EMAIL = "user@micromart.com";
    private final String ORDER_NUM = "ORD-123456";

    @BeforeEach
    void setUp() {
        // Setup Line Item (Quantity 2 x $50.00 = $100.00)
        lineItem = new OrderLineItems();
        lineItem.setSkuCode("SKU-123");
        lineItem.setQuantity(2);
        lineItem.setUnitPrice(new BigDecimal("50.00"));

        // Setup Request
        orderRequest = new OrderRequest();
        orderRequest.setUserEmail(AUTH_EMAIL);
        orderRequest.setItems(new ArrayList<>());

        // Setup Entity
        mockOrder = new Order(
                ORDER_NUM,
                AUTH_EMAIL,
                new BigDecimal("100.00"),
                OrderStatus.PENDING,
                List.of(lineItem)
        );

        // Setup Response
        orderResponse = new OrderResponse();
        orderResponse.setOrderNumber(ORDER_NUM);
        orderResponse.setUserEmail(AUTH_EMAIL);
        orderResponse.setOrderStatus(OrderStatus.PENDING);
    }

    // ==========================================
    // CREATE ORDER TESTS
    // ==========================================

    @Test
    @DisplayName("createOrder - Success: Calculates total correctly and saves order")
    void createOrder_Success_CalculatesTotal() {
        // Arrange
        when(orderMapper.mapToEntityList(any())).thenReturn(List.of(lineItem));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderResponse);

        // Act
        OrderResponse result = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(result);

        // Capture the order saved to the DB to verify the backend calculation logic
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();
        assertEquals(AUTH_EMAIL, capturedOrder.getUserEmail());
        assertEquals(OrderStatus.PENDING, capturedOrder.getOrderStatus());

        // Ensure 2 * 50.00 = 100.00 was calculated correctly by the backend
        assertEquals(new BigDecimal("100.00"), capturedOrder.getTotalAmount());
    }

    // ==========================================
    // GET ORDER / SECURITY GUARD TESTS
    // ==========================================

    @Test
    @DisplayName("getOrderByOrderNumber - Success: Returns order when user matches")
    void getOrderByOrderNumber_Success() {
        // Arrange
        when(orderRepository.findByOrderNumber(ORDER_NUM)).thenReturn(Optional.of(mockOrder));
        when(orderMapper.mapToResponse(mockOrder)).thenReturn(orderResponse);

        // Act
        OrderResponse result = orderService.getOrderByOrderNumber(ORDER_NUM, AUTH_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(ORDER_NUM, result.getOrderNumber());
    }

    @Test
    @DisplayName("getOrderByOrderNumber - Failure: Throws AccessDenied when email mismatches")
    void getOrderByOrderNumber_AccessDenied_ThrowsException() {
        // Arrange
        when(orderRepository.findByOrderNumber(ORDER_NUM)).thenReturn(Optional.of(mockOrder));

        // Act & Assert
        OrderAccessDeniedException exception = assertThrows(OrderAccessDeniedException.class, () ->
                orderService.getOrderByOrderNumber(ORDER_NUM, "hacker@evil.com")
        );

        assertTrue(exception.getMessage().contains("do not have access"));
        verify(orderMapper, never()).mapToResponse(any());
    }

    @Test
    @DisplayName("getOrderByOrderNumber - Failure: Throws NotFound when order doesn't exist")
    void getOrderByOrderNumber_NotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findByOrderNumber("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () ->
                orderService.getOrderByOrderNumber("INVALID", AUTH_EMAIL)
        );
    }

    // ==========================================
    // CANCEL ORDER TESTS
    // ==========================================

    @Test
    @DisplayName("cancelOrder - Success: Cancels PENDING order successfully")
    void cancelOrder_Success() {
        // Arrange
        when(orderRepository.findByOrderNumber(ORDER_NUM)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        orderResponse.setOrderStatus(OrderStatus.CANCELLED);
        when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderResponse);

        // Act
        OrderResponse result = orderService.cancelOrder(ORDER_NUM, CancellationReason.CUSTOMER_REQUEST, AUTH_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getOrderStatus());

        // Verify entity was mutated before save
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.CANCELLED, orderCaptor.getValue().getOrderStatus());
        assertEquals(CancellationReason.CUSTOMER_REQUEST, orderCaptor.getValue().getCancellationReason());
    }

    @Test
    @DisplayName("cancelOrder - Failure: Prevents cancellation of SHIPPED orders")
    void cancelOrder_AlreadyShipped_ThrowsException() {
        // Arrange
        mockOrder.setOrderStatus(OrderStatus.SHIPPED); // Set invalid state
        when(orderRepository.findByOrderNumber(ORDER_NUM)).thenReturn(Optional.of(mockOrder));

        // Act & Assert
        OrderCancellationException exception = assertThrows(OrderCancellationException.class, () ->
                orderService.cancelOrder(ORDER_NUM, CancellationReason.CUSTOMER_REQUEST, AUTH_EMAIL)
        );

        assertTrue(exception.getMessage().contains("already been shipped or delivered"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("cancelOrder - Failure: Throws AccessDenied for unauthorized cancellation attempt")
    void cancelOrder_AccessDenied_ThrowsException() {
        // Arrange
        when(orderRepository.findByOrderNumber(ORDER_NUM)).thenReturn(Optional.of(mockOrder));

        // Act & Assert
        assertThrows(OrderAccessDeniedException.class, () ->
                orderService.cancelOrder(ORDER_NUM, CancellationReason.CUSTOMER_REQUEST, "otheruser@mail.com")
        );

        verify(orderRepository, never()).save(any());
    }

    // ==========================================
    // PAGINATION TEST
    // ==========================================

    @Test
    @DisplayName("getUserOrders - Success: Returns paginated response")
    void getUserOrders_Success() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(mockOrder));

        when(orderRepository.findByUserEmailOrderByCreatedAtDesc(AUTH_EMAIL, pageable)).thenReturn(orderPage);
        when(orderMapper.mapToResponse(mockOrder)).thenReturn(orderResponse);

        // Act
        Page<OrderResponse> result = orderService.getUserOrders(AUTH_EMAIL, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(ORDER_NUM, result.getContent().get(0).getOrderNumber());
    }
}