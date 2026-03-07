package com.micromart.Payment.strategies.CryptoPaymentStrategy;

import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.model.dto.OrderDto;
import com.micromart.Payment.model.response.PaymentResponse;
import com.micromart.Payment.strategies.PaymentStrategy;
import com.micromart.Payment.util.PaymentUtils;
import org.springframework.stereotype.Service;

@Service
public class CryptoPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResponse initiate(OrderDto order) {

        String walletAddress = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e";

        String mockTxHash = PaymentUtils.generateMockTxHash();

        String instructions = String.format(
                "ORDER #%s PLACED. \n" +
                        "Please send exactly %s %s to the wallet address below. \n" +
                        "Wallet Address: %s \n" +
                        "Mock System Note: Your auto-generated TxHash is %s. " +
                        "The admin will use this to approve your order.",
                order.getOrderId(),
                order.getTotalAmount(),
                order.getCurrency(),
                walletAddress,
                mockTxHash
        );

        String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + walletAddress;

        return PaymentResponse.builder()
                .paymentUrl(qrCodeUrl)
                .instructions(instructions)
                .status("AWAITING_ADMIN_APPROVAL")
                .sessionId(mockTxHash)
                .build();
    }

    @Override
    public PaymentMethod getType() {
        return PaymentMethod.CRYPTO;
    }
}
