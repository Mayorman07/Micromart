package com.micromart.notification.model;

import com.micromart.notification.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEvent implements Serializable {
    private String orderId;
    private String userId;
    private Status status;
    private String paymentMethod;
}
