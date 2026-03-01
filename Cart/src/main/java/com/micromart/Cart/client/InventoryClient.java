package com.micromart.Cart.client;

import com.micromart.Cart.model.responses.InventoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
public class InventoryClient {

    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(InventoryClient.class);
    private static final String INVENTORY_SERVICE_URL = "http://inventory/api/inventory/sku-code/";
    public InventoryClient(WebClient.Builder webClientBuilder){
        this.webClientBuilder = webClientBuilder;
    }

    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        logger.info("Calling Inventory Service to check stock for SKUs: {}", skuCodes);
        String joinedSkuCodes = String.join(",", skuCodes);

        return webClientBuilder.build()
                .get()
                .uri(INVENTORY_SERVICE_URL + "{sku-code}", joinedSkuCodes)
                .retrieve()
                .bodyToFlux(InventoryResponse.class)
                .collectList()
                .block();
    }
}
