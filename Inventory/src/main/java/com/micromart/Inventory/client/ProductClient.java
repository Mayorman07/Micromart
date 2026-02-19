package com.micromart.Inventory.client;

import com.micromart.Inventory.model.meta.ProductMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class ProductClient {

    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(ProductClient.class);

    public ProductClient(WebClient.Builder webClientBuilder){
        this.webClientBuilder = webClientBuilder;

    }

    public Flux<ProductMetadata> getMetadataBatch(List<String> skuCodes) {
        return webClientBuilder.build().post()
                .uri("http://Products/products/metadata/batch")
                .bodyValue(skuCodes)
                .retrieve()
                .bodyToFlux(ProductMetadata.class)
                .onErrorResume(e -> {
                    logger.error("Batch fetch failed: {}", e.getMessage());
                    return Flux.empty();
                });
    }
}
