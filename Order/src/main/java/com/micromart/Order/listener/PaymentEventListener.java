package com.micromart.Order.listener;

import com.micromart.Order.entities.Order;
import com.micromart.Order.enums.OrderStatus;
import com.micromart.Order.events.OrderEventPayloads;
import com.micromart.Order.events.PaymentEvent;
import com.micromart.Order.publisher.OrderEventPublisher;
import com.micromart.Order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventListener.class);
    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @RabbitListener(queues = "payment.success.queue")
    @Transactional
    public void handlePaymentSuccess(PaymentEvent paymentEvent) {
        logger.info("Received PaymentEvent for Order ID: {} with status: {}", paymentEvent.getOrderId(), paymentEvent.getStatus());

        if (!"SUCCESS".equalsIgnoreCase(paymentEvent.getStatus())) {
            logger.warn("Payment was not successful for Order {}. Ignoring Ripple Effect.", paymentEvent.getOrderId());
            return;
        }

        Order order = orderRepository.findByOrderNumber(paymentEvent.getOrderId()) // 🎯 UPDATED GETTER
                .orElseThrow(() -> new RuntimeException("Order not found during payment processing: " + paymentEvent.getOrderId()));

        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);

        logger.info("Order {} updated to PAID status.", order.getOrderNumber());

        List<OrderEventPayloads.EventLineItem> eventItems = order.getOrderLineItemsList().stream()
                .map(item -> OrderEventPayloads.EventLineItem.builder()
                        .skuCode(item.getSkuCode())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        // 3. Trigger the Ripple Effect
        eventPublisher.publishDeductStockEvent(new OrderEventPayloads.DeductStockEvent(order.getOrderNumber(), eventItems));
        eventPublisher.publishClearCartEvent(new OrderEventPayloads.ClearCartEvent(order.getUserEmail()));
        eventPublisher.publishOrderReceiptEvent(new OrderEventPayloads.OrderReceiptEvent(order.getOrderNumber(), order.getUserEmail(), order.getTotalAmount(), eventItems));
    }
}
