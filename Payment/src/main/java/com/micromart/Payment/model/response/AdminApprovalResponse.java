package com.micromart.Payment.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminApprovalResponse {
    private boolean success;
    private String message;
    private String orderId;
    private String reference;
}