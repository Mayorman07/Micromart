package com.micromart.Payment.controller;

import com.micromart.Payment.model.dto.OrderDto;
import com.micromart.Payment.model.request.PaymentRequest;
import com.micromart.Payment.model.response.PaymentResponse;
import com.micromart.Payment.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(@AuthenticationPrincipal String userId, @RequestBody PaymentRequest paymentRequest){
        logger.info("The incoming create payment request {} " , paymentRequest);
        PaymentResponse orderToBePaid = paymentService.processPayment(userId,paymentRequest);
        logger.info("The outgoing create payment response {} " , orderToBePaid);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderToBePaid);

    }

}
