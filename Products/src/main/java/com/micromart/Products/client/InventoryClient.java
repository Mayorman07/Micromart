package com.micromart.products.client;

import com.micromart.products.model.requests.InventoryRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class InventoryClient {

    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(InventoryClient.class);

    public InventoryClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    public void initializeStock(String skuCode, Integer quantity) {
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setSkuCode(skuCode);
        inventoryRequest.setQuantity(quantity != null ? quantity : 0);

        try {
            webClientBuilder.build().post()
                    .uri("http://inventory/api/inventory/create")
                    .bodyValue(inventoryRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Inventory initialized for SKU: {}", skuCode);
        } catch (Exception e) {
            logger.error("Failed to initialize inventory for SKU: " + skuCode, e);
           throw new RuntimeException("Inventory Service unavailable");
        }
    }
}