package com.micromart.Payment.strategies;

import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.model.dto.OrderDto;
import com.micromart.Payment.model.response.PaymentResponse;

public interface PaymentStrategy {
    PaymentResponse initiate(OrderDto order);
    PaymentMethod getType();
}
