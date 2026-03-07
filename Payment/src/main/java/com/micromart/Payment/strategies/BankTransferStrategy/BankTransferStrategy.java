package com.micromart.Payment.strategies.BankTransferStrategy;

import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.model.dto.OrderDto;
import com.micromart.Payment.model.response.PaymentResponse;
import com.micromart.Payment.strategies.PaymentStrategy;
import org.springframework.stereotype.Service;

@Service
public class BankTransferStrategy implements PaymentStrategy {
    @Override
    public PaymentResponse initiate(OrderDto order) {
        String mockInstructions = "Please transfer " + order.getTotalAmount() +
                " to: MicroMart Bank | Acct: 1234567890 | Bank: TeekaBank";

        return new PaymentResponse(null, mockInstructions, "PENDING_VERIFICATION");
    }

    @Override
    public PaymentMethod getType() {
        return PaymentMethod.BANK_TRANSFER;
    }
}
