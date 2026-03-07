package com.micromart.Payment.strategies.StripePaymentStrategy;

import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.enums.Status;
import com.micromart.Payment.model.dto.OrderDto;
import com.micromart.Payment.model.dto.OrderItemDto;
import com.micromart.Payment.model.response.PaymentResponse;
import com.micromart.Payment.strategies.PaymentStrategy;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class StripePaymentStrategy implements PaymentStrategy {
    @Value("${stripe.secret.key}")
    private String secretKey;
    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public PaymentResponse initiate(OrderDto order) {
        try {
            long expirationTime = Instant.now().plus(2, ChronoUnit.HOURS).getEpochSecond();
            SessionCreateParams.Builder sessionBuilder = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?orderId=" + order.getOrderId())
                    .setCancelUrl(cancelUrl)
                    .putMetadata("orderId", order.getOrderId())
                    .setCustomerEmail(order.getUserEmail())
                    .setExpiresAt(expirationTime);

            for (OrderItemDto item : order.getItems()) {
                SessionCreateParams.LineItem.PriceData.ProductData.Builder productDataBuilder =
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(item.getProductName());

                if (item.getImageUrl() != null && !item.getImageUrl().isBlank()) {
                    productDataBuilder.addImage(item.getImageUrl());
                }

                sessionBuilder.addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(item.getQuantity().longValue())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(item.getUnitPrice().multiply(new BigDecimal(100)).longValue())
                                                .setProductData(productDataBuilder.build())
                                                .build()
                                )
                                .build()
                );
            }

            Session session = Session.create(sessionBuilder.build());

            return new PaymentResponse(
                    session.getUrl(),
                    "Redirecting...",
                    Status.PENDING,
                    session.getId()
            );

        } catch (StripeException e) {
            throw new RuntimeException("Stripe error: " + e.getMessage());
        }
    }

    @Override
    public PaymentMethod getType() {
        return PaymentMethod.STRIPE;
    }
}