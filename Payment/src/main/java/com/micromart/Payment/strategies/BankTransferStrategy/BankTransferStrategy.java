package com.micromart.Payment.strategies.BankTransferStrategy;

import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.enums.Status;
import com.micromart.Payment.model.dto.OrderDto;
import com.micromart.Payment.model.response.PaymentResponse;
import com.micromart.Payment.strategies.PaymentStrategy;
import com.micromart.Payment.util.PaymentUtils;
import org.springframework.stereotype.Service;

@Service
public class BankTransferStrategy implements PaymentStrategy {

    @Override
    public PaymentResponse initiate(OrderDto order) {
        String transferReference = PaymentUtils.generateBankTransferReference();

        // We keep the instructions clean for the frontend
        String instructions = String.format(
                "Bank: Mayorman Microfinance Bank\n" +
                        "Account: 0127753007\n" +
                        "Name: MicroMart Technologies Ltd");

        return new PaymentResponse(
                null,
                instructions,
                Status.AWAITING_TRANSFER,
                transferReference
        );
    }

    @Override
    public PaymentMethod getType() {
        return PaymentMethod.BANK_TRANSFER;
    }
}