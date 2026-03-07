package com.micromart.Payment.model.response;

import com.micromart.Payment.enums.Status;
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
    private Status status;
    private String sessionId;
    private String clientSecret;

    public PaymentResponse(String paymentUrl, String instructions, Status status) {
        this.paymentUrl = paymentUrl;
        this.instructions = instructions;
        this.status = status;
    }
}
