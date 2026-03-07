package com.micromart.notification.client;

import com.micromart.notification.model.OrderSummaryDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class OrderClient {

    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(OrderClient.class);

    public OrderClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Fetches the order details from the Order Service to enrich the notification.
     */
    public OrderSummaryDTO getOrderSummary(String orderId) {
        logger.info("Requesting order summary from Order Service for ID: {}", orderId);

        try {
            return webClientBuilder.build().get()
                    .uri("http://order-service/api/internal/orders/{id}/summary", orderId)
                    .retrieve()
                    .bodyToMono(OrderSummaryDTO.class)
                    .block(); // We block because the email cannot be sent without this data
        } catch (Exception e) {
            logger.error(" Failed to fetch order summary for Order: " + orderId, e);
            // Throwing this ensures the RabbitMQ listener retries the message
            throw new RuntimeException("Order Service enrichment unavailable");
        }
    }
}