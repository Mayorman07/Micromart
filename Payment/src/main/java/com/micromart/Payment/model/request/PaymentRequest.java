package com.micromart.Payment.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    private String orderId;
    private String userEmail;
    private BigDecimal totalAmount;
    private String currency;
    private List<OrderItemRequest> items;
    private String paymentMethod;
}