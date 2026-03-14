package com.micromart.Order.listener;

import com.micromart.Order.entities.Order;
import com.micromart.Order.enums.OrderStatus;
import com.micromart.Order.events.OrderEventPayloads;
import com.micromart.Order.events.PaymentEvent;
import com.micromart.Order.publisher.OrderEventPublisher;
import com.micromart.Order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component

public class PaymentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventListener.class);
    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public PaymentEventListener(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "payment.success.queue", durable = "true"),
            exchange = @Exchange(value = "micromart.exchange", type = "topic"),
            key = "payment.status.updated"
    ))
    @Transactional
    public void handlePaymentSuccess(PaymentEvent paymentEvent) {
        logger.info("Received PaymentEvent for Order ID: {} with status: {}", paymentEvent.getOrderId(), paymentEvent.getStatus());

        if (!"PAID".equalsIgnoreCase(paymentEvent.getStatus())) {
            logger.warn("Payment is not PAID for Order {}. Current status is {}. Ignoring Ripple Effect.",
                    paymentEvent.getOrderId(), paymentEvent.getStatus());
            return;
        }

        Order order = orderRepository.findByOrderNumber(paymentEvent.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found during payment processing: " + paymentEvent.getOrderId()));

        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);

        logger.info("Order {} updated to PAID status.", order.getOrderNumber());

        List<OrderEventPayloads.EventLineItem> eventItems = order.getOrderLineItemsList().stream()
                .map(item -> new OrderEventPayloads.EventLineItem(
                        item.getSkuCode(),
                        item.getProductName(),
                        item.getQuantity()
                ))
                .toList();

        eventPublisher.publishDeductStockEvent(new OrderEventPayloads.DeductStockEvent(order.getOrderNumber(), eventItems));
        eventPublisher.publishClearCartEvent(new OrderEventPayloads.ClearCartEvent(order.getUserEmail()));
        eventPublisher.publishOrderReceiptEvent(new OrderEventPayloads.OrderReceiptEvent(order.getOrderNumber(), order.getUserEmail(), order.getTotalAmount(), eventItems));
    }
}
