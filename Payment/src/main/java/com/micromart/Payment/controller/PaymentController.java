package com.micromart.Payment.controller;

import com.micromart.Payment.model.request.PaymentRequest;
import com.micromart.Payment.model.response.PaymentResponse;
import com.micromart.Payment.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    public PaymentController(PaymentService paymentService){
        this.paymentService=paymentService;

    }

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(@AuthenticationPrincipal String userId, @RequestBody @Valid PaymentRequest paymentRequest){
        logger.info("The incoming create payment request {} " , paymentRequest);
        PaymentResponse orderToBePaid = paymentService.processPayment(userId,paymentRequest);
        logger.info("The outgoing create payment response {} " , orderToBePaid);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderToBePaid);

    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approveManualPayment(@RequestParam("reference") String reference) {

        logger.info("Admin approving payment | reference: {}", reference);

        String result = paymentService.approveManualPayment(reference);

        return ResponseEntity.ok(result);
    }
}

