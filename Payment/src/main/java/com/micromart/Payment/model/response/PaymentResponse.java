package com.micromart.Payment.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PaymentResponse {
    private String paymentUrl;
    private String instructions;
    private String status;

    private String paymentIntentId;

    private String clientSecret;

    public PaymentResponse(String paymentUrl, String instructions, String status) {
        this.paymentUrl = paymentUrl;
        this.instructions = instructions;
        this.status = status;
    }
}
