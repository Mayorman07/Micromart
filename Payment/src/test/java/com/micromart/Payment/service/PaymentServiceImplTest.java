package com.micromart.Payment.service;

import com.micromart.Payment.entity.PaymentRecord;
import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.enums.Status;
import com.micromart.Payment.exceptions.ResourceNotFoundException;
import com.micromart.Payment.factory.PaymentFactory;
import com.micromart.Payment.messaging.PaymentEvent;
import com.micromart.Payment.messaging.PaymentStatusPublisher;
import com.micromart.Payment.model.dto.OrderDto;
import com.micromart.Payment.model.request.PaymentRequest;
import com.micromart.Payment.model.response.PaymentResponse;
import com.micromart.Payment.repository.PaymentRecordRepository;
import com.micromart.Payment.services.PaymentServiceImpl;
import com.micromart.Payment.strategies.PaymentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private PaymentFactory paymentFactory;
    @Mock private ModelMapper modelMapper;
    @Mock private PaymentRecordRepository paymentRecordRepository;
    @Mock private PaymentStatusPublisher paymentStatusPublisher;
    @Mock private PaymentStrategy paymentStrategy;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest paymentRequest;
    private PaymentRecord paymentRecord;
    private OrderDto orderDto;
    private PaymentResponse successResponse;

    @BeforeEach
    void setUp() {
        // Setup input request
        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId("ORD-123");
        paymentRequest.setTotalAmount(BigDecimal.valueOf(150.00));
        paymentRequest.setCurrency("USD");
        paymentRequest.setPaymentMethod("STRIPE");
        paymentRequest.setItems(new ArrayList<>());

        // Setup mapped OrderDto
        orderDto = new OrderDto();
        orderDto.setOrderId("ORD-123");

        // Setup mocked DB record
        paymentRecord = new PaymentRecord(
                "ORD-123", "user-uuid", BigDecimal.valueOf(150.00),
                "USD", PaymentMethod.STRIPE, Status.PENDING
        );
        paymentRecord.setId(1L); // Simulate saved DB ID

        successResponse = new PaymentResponse(
                "PAY-999",               // 1st: Order/Payment ID
                "https://checkout.url",  // 2nd: Checkout URL
                Status.PENDING,          // 3rd: Status
                "cs_test_12345"          // 4th: Session ID
        );
    }

    // ==========================================
    // PROCESS PAYMENT TESTS
    // ==========================================

    @Test
    @DisplayName("processPayment - Success: Maps request, saves record, invokes strategy, and updates session ID")
    void processPayment_Success() {
        // Arrange
        when(modelMapper.map(any(PaymentRequest.class), eq(OrderDto.class))).thenReturn(orderDto);

        // Mock repo save to just return the object passed to it
        when(paymentRecordRepository.save(any(PaymentRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(paymentFactory.getStrategy(PaymentMethod.STRIPE)).thenReturn(paymentStrategy);
        when(paymentStrategy.initiate(any(OrderDto.class))).thenReturn(successResponse);

        // Act
        PaymentResponse result = paymentService.processPayment("user-uuid", paymentRequest);

        // Assert
        assertNotNull(result);
        assertEquals("cs_test_12345", result.getSessionId());

        // Verify save was called TWICE: 1. Initial PENDING save, 2. Update with Session ID
        verify(paymentRecordRepository, times(2)).save(any(PaymentRecord.class));
        verify(paymentStrategy, times(1)).initiate(any(OrderDto.class));
    }

    @Test
    @DisplayName("processPayment - Failure: Rolls back internal payment record to FAILED status on Exception")
    void processPayment_StrategyFails_RollsBackToFailed() {
        // Arrange
        when(modelMapper.map(any(PaymentRequest.class), eq(OrderDto.class))).thenReturn(orderDto);
        when(paymentRecordRepository.save(any(PaymentRecord.class))).thenReturn(paymentRecord);
        when(paymentFactory.getStrategy(PaymentMethod.STRIPE)).thenReturn(paymentStrategy);

        // Force the strategy to throw an error
        when(paymentStrategy.initiate(any(OrderDto.class))).thenThrow(new RuntimeException("Stripe API down"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                paymentService.processPayment("user-uuid", paymentRequest)
        );

        assertTrue(exception.getMessage().contains("Payment service unavailable"));

        // Verify rollback logic captured the error and saved status as FAILED
        ArgumentCaptor<PaymentRecord> recordCaptor = ArgumentCaptor.forClass(PaymentRecord.class);
        verify(paymentRecordRepository, times(2)).save(recordCaptor.capture());

        PaymentRecord finalSavedRecord = recordCaptor.getAllValues().get(1);
        assertEquals(Status.FAILED, finalSavedRecord.getStatus());
        assertEquals("Stripe API down", finalSavedRecord.getErrorMessage());
    }

    // ==========================================
    // MANUAL PAYMENT APPROVAL TESTS
    // ==========================================

    @Test
    @DisplayName("approveManualPayment - Success: Changes status to PAID and publishes event")
    void approveManualPayment_Success() {
        // Arrange
        paymentRecord.setStatus(Status.AWAITING_TRANSFER);
        paymentRecord.setExternalReference("MANUAL-REF-999");
        when(paymentRecordRepository.findByExternalReference("MANUAL-REF-999")).thenReturn(Optional.of(paymentRecord));

        // Act
        String resultMessage = paymentService.approveManualPayment("MANUAL-REF-999");

        // Assert
        assertTrue(resultMessage.contains("Successfully approved"));
        assertEquals(Status.PAID, paymentRecord.getStatus());

        verify(paymentRecordRepository, times(1)).save(paymentRecord);

        // Verify Message Publisher was triggered with correct status
        ArgumentCaptor<PaymentEvent> eventCaptor = ArgumentCaptor.forClass(PaymentEvent.class);
        verify(paymentStatusPublisher, times(1)).publishPaymentStatus(eventCaptor.capture());

        PaymentEvent publishedEvent = eventCaptor.getValue();
        assertEquals("ORD-123", publishedEvent.getOrderId());
        assertEquals(Status.PAID, publishedEvent.getStatus());
    }

    @Test
    @DisplayName("approveManualPayment - Failure: Throws Exception if record not found")
    void approveManualPayment_RecordNotFound_ThrowsException() {
        // Arrange
        when(paymentRecordRepository.findByExternalReference("INVALID-REF")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                paymentService.approveManualPayment("INVALID-REF")
        );

        verify(paymentRecordRepository, never()).save(any());
        verify(paymentStatusPublisher, never()).publishPaymentStatus(any());
    }

    @Test
    @DisplayName("approveManualPayment - Failure: Throws IllegalStateException if payment is already Paid/Failed")
    void approveManualPayment_InvalidState_ThrowsException() {
        // Arrange
        paymentRecord.setStatus(Status.PAID);
        when(paymentRecordRepository.findByExternalReference("REF-123")).thenReturn(Optional.of(paymentRecord));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                paymentService.approveManualPayment("REF-123")
        );

        assertTrue(exception.getMessage().contains("Payment is not in an approvable state"));
        verify(paymentRecordRepository, never()).save(any());
    }
}
