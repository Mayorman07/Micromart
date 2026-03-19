package com.micromart.Payment.controller;

import com.micromart.Payment.services.PaymentService;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class WebhookController {

    private final PaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    public WebhookController(PaymentService paymentService){
        this.paymentService=paymentService;
    }


    @PostMapping("/webhook")
    public ResponseEntity<String> processStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) throws SignatureVerificationException, EventDataObjectDeserializationException {

        logger.info("Received Stripe Webhook request");

        paymentService.processStripeWebhook(payload, sigHeader);

        return ResponseEntity.ok("Webhook processed successfully");
    }
}
