package com.micromart.Payment.messaging;

import com.micromart.Payment.configuration.RabbitMQConfig;
import com.micromart.Payment.services.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentStatusPublisher {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(PaymentStatusPublisher.class);

    public PaymentStatusPublisher(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate=rabbitTemplate;
    }


    /**
     * Publishes a payment status update to the RabbitMQ exchange.
     * This will be picked up by the Order Service to update order status.
     */
    public void publishPaymentStatus(PaymentEvent event) {
        logger.info("Publishing Payment Event for Order ID: {} with Status: {}",
                event.getOrderId(), event.getStatus());

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    event
            );
            logger.info("Successfully sent event to RabbitMQ");
        } catch (Exception e) {
            logger.error("Failed to send payment event to RabbitMQ: {}", e.getMessage());

        }
    }
}
