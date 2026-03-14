package com.micromart.Payment.strategies.CryptoPaymentStrategy;

import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.enums.Status;
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
                "Network: USDT (ERC20)\n" +
                        "Address: %s\n" +
                        "Amount: %s %s",
                walletAddress,
                order.getTotalAmount(),
                order.getCurrency()
        );

        String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + walletAddress;

        return new PaymentResponse(
                qrCodeUrl,
                instructions,
                Status.AWAITING_ADMIN_APPROVAL,
                mockTxHash
        );
    }

    @Override
    public PaymentMethod getType() {
        return PaymentMethod.CRYPTO;
    }
}
