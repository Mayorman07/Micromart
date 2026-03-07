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
        String amountToPay = order.getTotalAmount().toString();
        String transferReference = PaymentUtils.generateBankTransferReference();

        String instructions = String.format(
                "ORDER #%s PLACED. \n" +
                        "Please transfer %s %s to: \n" +
                        "Bank: MicroMart Microfinance Bank \n" +
                        "Account Number: 0123456789 \n" +
                        "CRITICAL: Use this Reference in your transfer description: %s",
                order.getOrderId(),
                order.getCurrency(),
                amountToPay,
                transferReference
        );

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