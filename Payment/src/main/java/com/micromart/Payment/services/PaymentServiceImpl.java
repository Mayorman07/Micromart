package com.micromart.Payment.services;

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
import com.micromart.Payment.strategies.PaymentStrategy;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import com.micromart.Payment.entity.PaymentRecord;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final PaymentFactory paymentFactory;
    private final ModelMapper modelMapper;
    private final PaymentRecordRepository paymentRecordRepository;
    private final PaymentStatusPublisher paymentStatusPublisher;
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public PaymentResponse processPayment(String userId, PaymentRequest paymentRequest) {

        logger.info("Processing payment | orderId: {}, userId: {}, method: {}",
                paymentRequest.getOrderId(), userId, paymentRequest.getPaymentMethod());

        PaymentRecord paymentRecord = null;
        PaymentResponse response = null;

        try {
            OrderDto orderDto = modelMapper.map(paymentRequest, OrderDto.class);
            orderDto.setUserId(userId);
            orderDto.setStatus(Status.PENDING);

            PaymentMethod method = PaymentMethod.valueOf(
                    paymentRequest.getPaymentMethod().toUpperCase()
            );


            paymentRecord = PaymentRecord.builder()
                    .orderId(paymentRequest.getOrderId())
                    .userId(userId)
                    .amount(paymentRequest.getTotalAmount())
                    .currency(paymentRequest.getCurrency())
                    .paymentMethod(method)
                    .status(Status.PENDING)
                    .build();

            paymentRecord = paymentRecordRepository.save(paymentRecord);
            logger.info("Saved PENDING payment record | internalId: {}, orderId: {}",
                    paymentRecord.getId(), paymentRequest.getOrderId());

            PaymentStrategy strategy = paymentFactory.getStrategy(method);
            response = strategy.initiate(orderDto);

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

            if (paymentRecord != null && paymentRecord.getId() != null) {
                try {
                    paymentRecord.setStatus(Status.FAILED);
                    paymentRecord.setErrorMessage(e.getMessage());
                    paymentRecordRepository.save(paymentRecord);
                    logger.info("Rolled back payment record {} to FAILED state.", paymentRecord.getId());
                } catch (Exception ex) {
                    logger.error("CRITICAL: Failed to update payment record status to FAILED", ex);
                }
            }
            throw new RuntimeException("Payment service unavailable", e);
        }
    }

    @Override
    @Transactional
    public String approveManualPayment(String reference) {

        logger.info("Admin attempting to approve payment with reference: {}", reference);

        PaymentRecord paymentRecord = paymentRecordRepository.findByExternalReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for reference: " + reference));

        if (paymentRecord.getStatus() != Status.AWAITING_TRANSFER &&
                paymentRecord.getStatus() != Status.AWAITING_ADMIN_APPROVAL) {
            logger.warn("Cannot approve payment {} - current status: {}", reference, paymentRecord.getStatus());
            throw new IllegalStateException("Payment is not in an approvable state: " + paymentRecord.getStatus());
        }
        paymentRecord.setStatus(Status.PAID);
        paymentRecordRepository.save(paymentRecord);

        logger.info("Successfully approved payment for Order: {}", paymentRecord.getOrderId());

        sendStatusUpdate(paymentRecord);
        return "Successfully approved payment for Order: " + paymentRecord.getOrderId();
    }

    @Override
    @Transactional
    public void processStripeWebhook(String payload, String sigHeader) throws SignatureVerificationException {

        Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);

        if (stripeObject instanceof Session session) {
            String sessionId = session.getId();
            logger.info("Processing Webhook for Stripe Session: {}", sessionId);

            PaymentRecord paymentRecord = paymentRecordRepository.findByExternalReference(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("No internal record found for Stripe Session: " + sessionId));

            switch (event.getType()) {
                case "checkout.session.completed" -> {
                    if (paymentRecord.getStatus() == Status.PENDING) {
                        paymentRecord.setStatus(Status.PAID);
                        paymentRecordRepository.save(paymentRecord);
                        logger.info("Order {} marked as COMPLETED via Webhook!", paymentRecord.getOrderId());
                        sendStatusUpdate(paymentRecord);
                    }
                }
                case "checkout.session.expired" -> {
                    if (paymentRecord.getStatus() == Status.PENDING) {
                        paymentRecord.setStatus(Status.EXPIRED);
                        paymentRecord.setErrorMessage("Stripe Checkout Session Expired");
                        paymentRecordRepository.save(paymentRecord);
                        logger.info("Order {} marked as CANCELLED via Webhook expiration.", paymentRecord.getOrderId());
                        sendStatusUpdate(paymentRecord);
                    }
                }
                default -> logger.info("Unhandled Stripe event type: {}", event.getType());
            }
        }
    }

    private void sendStatusUpdate(PaymentRecord record) {
        PaymentEvent event = PaymentEvent.builder()
                .orderId(record.getOrderId())
                .userId(record.getUserId())
                .status(record.getStatus())
                .paymentMethod(record.getPaymentMethod().name())
                .build();

        paymentStatusPublisher.publishPaymentStatus(event);
    }
}
