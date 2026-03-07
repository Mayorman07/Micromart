package com.micromart.Payment.messaging;

import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@NoArgsConstructor
@Builder
public class PaymentEvent implements Serializable {
    private String orderId;
    private String userId;
    private Status status;
    private PaymentMethod paymentMethod;

    public PaymentEvent(String orderId, String userId, Status status, PaymentMethod paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
