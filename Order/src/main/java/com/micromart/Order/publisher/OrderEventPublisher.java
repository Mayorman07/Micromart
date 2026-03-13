package com.micromart.Order.publisher;

import com.micromart.Order.configuration.RabbitMQConfig;
import com.micromart.Order.events.OrderEventPayloads;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public void publishDeductStockEvent(OrderEventPayloads.DeductStockEvent event) {
        logger.info("Publishing DeductStockEvent for Order: {}", event.getOrderNumber());
        rabbitTemplate.convertAndSend(RabbitMQConfig.MICROMART_EXCHANGE, "inventory.deduct", event);
    }

    public void publishClearCartEvent(OrderEventPayloads.ClearCartEvent event) {
        logger.info("Publishing ClearCartEvent for User: {}", event.getUserEmail());
        rabbitTemplate.convertAndSend(RabbitMQConfig.MICROMART_EXCHANGE, "cart.clear", event);
    }

    public void publishOrderReceiptEvent(OrderEventPayloads.OrderReceiptEvent event) {
        logger.info("Publishing OrderReceiptEvent for Order: {}", event.getOrderNumber());
        rabbitTemplate.convertAndSend(RabbitMQConfig.MICROMART_EXCHANGE, "notification.receipt", event);
    }
}