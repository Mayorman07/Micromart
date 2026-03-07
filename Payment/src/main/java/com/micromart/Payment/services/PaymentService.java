package com.micromart.Payment.services;

import com.micromart.Payment.model.dto.OrderDto;
import com.micromart.Payment.model.request.PaymentRequest;
import com.micromart.Payment.model.response.PaymentResponse;

public interface PaymentService {

    /**
     * Process a payment initiation request.
     * @param userId Authenticated user ID (from security context)
     * @param paymentRequest The validated order details from frontend
     * @return PaymentResponse with redirect URL or confirmation details
     */
    PaymentResponse processPayment(String userId, PaymentRequest paymentRequest);
}