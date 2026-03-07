package com.micromart.Payment.services;

import com.micromart.Payment.controller.PaymentController;
import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.enums.Status;
import com.micromart.Payment.factory.PaymentFactory;
import com.micromart.Payment.model.dto.OrderDto;
import com.micromart.Payment.model.request.PaymentRequest;
import com.micromart.Payment.model.response.PaymentResponse;
import com.micromart.Payment.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentFactory paymentFactory;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public PaymentResponse processPayment(String userId, PaymentRequest paymentRequest) {

        logger.info("Processing payment | orderId: {}, userId: {}, method: {}",
                paymentRequest.getOrderId(), userId, paymentRequest.getPaymentMethod());

        OrderDto orderDto = modelMapper.map(paymentRequest, OrderDto.class);
        orderDto.setUserId(userId);
        orderDto.setStatus(Status.PENDING);

        PaymentMethod method = PaymentMethod.valueOf(paymentRequest.getPaymentMethod().toUpperCase());
        PaymentStrategy strategy = paymentFactory.getStrategy(method);
        return strategy.initiate(orderDto);
    }
}
