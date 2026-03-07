package com.micromart.Payment.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.micromart.Payment.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class PaymentResponse {
    private String paymentUrl;
    private String instructions;
    private Status status;
    private String sessionId;
    private String clientSecret;

    public PaymentResponse(String paymentUrl, String instructions, Status status, String sessionId) {
        this.paymentUrl = paymentUrl;
        this.instructions = instructions;
        this.status = status;
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public Status getStatus() {
        return status;
    }
}
