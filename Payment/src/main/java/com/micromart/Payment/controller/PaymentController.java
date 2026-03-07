package com.micromart.Payment.controller;

import com.micromart.Payment.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);


    public void initiatePayment(){

    }
}
