package com.micromart.Payment.services;

import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.enums.Status;
import com.micromart.Payment.factory.PaymentFactory;
import com.micromart.Payment.model.dto.OrderDto;
import com.micromart.Payment.model.request.PaymentRequest;
import com.micromart.Payment.model.response.PaymentResponse;
import com.micromart.Payment.repository.PaymentRecordRepository;
import com.micromart.Payment.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import com.micromart.Payment.entity.PaymentRecord;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentFactory paymentFactory;
    private final ModelMapper modelMapper;
    private final PaymentRecordRepository paymentRecordRepository;
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
// 🎯 REMOVED @Transactional so your FAILED record actually survives the exception!
    public PaymentResponse processPayment(String userId, PaymentRequest paymentRequest) {

        logger.info("Processing payment | orderId: {}, userId: {}, method: {}",
                paymentRequest.getOrderId(), userId, paymentRequest.getPaymentMethod());

        // 1️⃣ Declare outside the try block so the catch block can access it
        PaymentRecord paymentRecord = null;
        PaymentResponse response = null;

        try {
            // 2️⃣ Map Request → Internal DTO
            OrderDto orderDto = modelMapper.map(paymentRequest, OrderDto.class);
            orderDto.setUserId(userId);
            orderDto.setStatus(Status.PENDING);

            PaymentMethod method = PaymentMethod.valueOf(
                    paymentRequest.getPaymentMethod().toUpperCase()
            );

            // 3️⃣ Create & Save Initial "PENDING" Record (Paper Trail)
            paymentRecord = PaymentRecord.builder()
                    .orderId(paymentRequest.getOrderId())
                    .userId(userId)
                    .amount(paymentRequest.getTotalAmount())
                    .currency(paymentRequest.getCurrency())
                    .paymentMethod(method)
                    .status("PENDING")
                    .build();

            // This saves to the DB immediately.
            paymentRecord = paymentRecordRepository.save(paymentRecord);
            logger.info("Saved PENDING payment record | internalId: {}, orderId: {}",
                    paymentRecord.getId(), paymentRequest.getOrderId());

            // 4️⃣ Call Strategy (e.g., Create Stripe Session)
            PaymentStrategy strategy = paymentFactory.getStrategy(method);
            response = strategy.initiate(orderDto);

            // 5️⃣ Update Record with External Session ID (if provided)
            if (response.getSessionId() != null && !response.getSessionId().isBlank()) {
                paymentRecord.setExternalReference(response.getSessionId());
                paymentRecordRepository.save(paymentRecord);
                logger.info("Linked external session {} to internal payment record {}",
                        response.getSessionId(), paymentRecord.getId());
            }

            logger.info("Payment initiated successfully | orderId: {}, status: {}",
                    paymentRequest.getOrderId(), response.getStatus());

            return response;

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid payment request | orderId: {}, error: {}",
                    paymentRequest.getOrderId(), e.getMessage());
            throw e;

        } catch (Exception e) {
            logger.error("Payment initiation failed | orderId: {}", paymentRequest.getOrderId(), e);

            // 🔥 Mark record as FAILED if it exists
            if (paymentRecord != null && paymentRecord.getId() != null) {
                try {
                    if (!"FAILED".equals(paymentRecord.getStatus())) {
                        paymentRecord.setStatus("FAILED");
                        paymentRecord.setErrorMessage(e.getMessage());
                        paymentRecordRepository.save(paymentRecord);
                        logger.info("Rolled back payment record {} to FAILED state.", paymentRecord.getId());
                    }
                } catch (Exception ex) {
                    logger.error("CRITICAL: Failed to update payment record status to FAILED", ex);
                }
            }

            // 🎯 Moved this OUTSIDE the if-statement.
            // Now, no matter when the crash happens, the user gets a proper 500 Error!
            throw new RuntimeException("Payment service unavailable", e);
        }
    }
}
