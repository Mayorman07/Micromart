package com.micromart.Payment.services;
import com.micromart.Payment.entity.PaymentRecord;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.micromart.Payment.model.request.PaymentRequest;
import com.micromart.Payment.model.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    /**
     * Process a payment initiation request.
     * @param userId Authenticated user ID (from security context)
     * @param paymentRequest The validated order details from frontend
     * @return PaymentResponse with redirect URL or confirmation details
     */
    PaymentResponse processPayment(String userId, PaymentRequest paymentRequest);

    /**
     * Admin endpoint to manually approve Bank and Crypto payments.
     * @param reference The unique BT-REF or TxHash
     * @return A success message
     */
    String approveManualPayment(String reference);

    List<PaymentRecord> getPendingManualPayments();
    void processStripeWebhook(String payload, String sigHeader) throws SignatureVerificationException;
}