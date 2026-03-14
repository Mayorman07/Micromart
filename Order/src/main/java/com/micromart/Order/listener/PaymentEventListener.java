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
        if (paymentEvent == null || paymentEvent.getOrderId() == null) {
            logger.error("Received a null or malformed PaymentEvent");
            return;
        }

        logger.info("Received PaymentEvent for Order ID: {} with status: {}",
                paymentEvent.getOrderId(), paymentEvent.getStatus());

        String status = String.valueOf(paymentEvent.getStatus());
        if (!"PAID".equalsIgnoreCase(status)) {
            logger.warn("Ignoring event. Status is: {}", status);
            return;
        }

        Order order = orderRepository.findByOrderNumber(paymentEvent.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + paymentEvent.getOrderId()));

        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);
        logger.info("Order {} marked as PAID in database", order.getOrderNumber());

        try {
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

            logger.info("Downstream events published successfully for Order {}", order.getOrderNumber());
        } catch (Exception e) {
            logger.error("Failed to publish downstream events for Order {}: {}", order.getOrderNumber(), e.getMessage());
            throw e;
        }
    }
}
