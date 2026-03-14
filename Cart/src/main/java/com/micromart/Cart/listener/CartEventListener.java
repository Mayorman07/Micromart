package com.micromart.Cart.listener;

import com.micromart.Cart.events.ClearCartEvent;
import com.micromart.Cart.services.CartService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartEventListener {

    private static final Logger logger = LoggerFactory.getLogger(CartEventListener.class);
    private final CartService cartService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "cart.clear.queue", durable = "true"),
            exchange = @Exchange(value = "micromart.exchange", type = "topic"),
            key = "cart.clear"
    ))
    public void handleClearCartEvent(ClearCartEvent event) {
        logger.info("Received ClearCartEvent from RabbitMQ for user: {}", event.getUserEmail());

        try {
            cartService.clearUserCart(event.getUserEmail());

            logger.info("Successfully cleared cart via event for user: {}", event.getUserEmail());
        } catch (Exception e) {
            logger.error("Could not clear cart for user {}. Reason: {}", event.getUserEmail(), e.getMessage());
        }
    }
}
