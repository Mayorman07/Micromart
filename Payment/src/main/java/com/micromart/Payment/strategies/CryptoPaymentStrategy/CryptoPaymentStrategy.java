package com.micromart.Payment.strategies.CryptoPaymentStrategy;

import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.model.dto.OrderDto;
import com.micromart.Payment.model.response.PaymentResponse;
import com.micromart.Payment.strategies.PaymentStrategy;
import org.springframework.stereotype.Service;

@Service
public class CryptoPaymentStrategy implements PaymentStrategy {
    @Override
    public PaymentResponse initiate(OrderDto order) {
        String walletAddress = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e";
        String instructions = "Send exactly " + order.getTotalAmount() + " USDT to " + walletAddress;

        return new PaymentResponse("https://api.qrserver.com/v1/create-qr-code/?data=" + walletAddress,
                instructions, "AWAITING_BLOCKCHAIN_CONFIRMATION");
    }

    @Override
    public PaymentMethod getType() {
        return PaymentMethod.CRYPTO;
    }
}