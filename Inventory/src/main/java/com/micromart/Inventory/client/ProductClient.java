package com.micromart.Inventory.client;

import com.micromart.Inventory.model.meta.ProductMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ProductClient {

    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(ProductClient.class);

    public ProductClient(WebClient.Builder webClientBuilder){
        this.webClientBuilder = webClientBuilder;

    }

    public ProductMetadata getProductMetadata(String skuCode) {
        try {
            return webClientBuilder.build().get()
                    .uri("http://Products/products/metadata/{sku}", skuCode)
                    .retrieve()
                    .bodyToMono(ProductMetadata.class)
                    .block();
        } catch (Exception e) {
            logger.error("Failed to fetch product metadata for SKU: {}. Error: {}", skuCode, e.getMessage());
            return new ProductMetadata("Unknown Product", java.math.BigDecimal.ZERO, "N/A");
        }
    }
}
