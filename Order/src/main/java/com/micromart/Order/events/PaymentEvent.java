package com.micromart.Order.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent implements Serializable {
    private String orderId;
    private String userId;
    private String status;
    private String paymentMethod;
}
