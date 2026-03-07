package com.micromart.Payment.services;

import com.micromart.Payment.model.dto.OrderDto;
import com.micromart.Payment.model.response.PaymentResponse;

public interface PaymentService {

    /**
     * Process a payment initiation request.
     * @param order The validated order details
     * @return PaymentResponse with redirect URL or confirmation details
     */
    PaymentResponse processPayment(OrderDto order);
}