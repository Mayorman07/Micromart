package com.micromart.products.client;

import com.micromart.products.model.requests.InventoryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryClient {

    private final WebClient.Builder webClientBuilder;

    public void initializeStock(String skuCode, Integer quantity) {
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setSkuCode(skuCode);
        inventoryRequest.setQuantity(quantity != null ? quantity : 0);

        try {
            webClientBuilder.build().post()
                    .uri("http://inventory-service/api/inventory/create")
                    .bodyValue(inventoryRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Inventory initialized for SKU: {}", skuCode);
        } catch (Exception e) {
            log.error("Failed to initialize inventory for SKU: " + skuCode, e);
           throw new RuntimeException("Inventory Service unavailable");
        }
    }
}