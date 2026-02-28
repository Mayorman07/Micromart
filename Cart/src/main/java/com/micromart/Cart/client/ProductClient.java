package com.micromart.Cart.client;

import com.micromart.Cart.model.meta.ProductMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class ProductClient {

    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(ProductClient.class);
    private static final String PRODUCT_SERVICE_URL = "http://product-service/products/metadata/batch";

    public ProductClient(WebClient.Builder webClientBuilder){
        this.webClientBuilder = webClientBuilder;

    }
    public List<ProductMetadata> getProductMetadataBatch(List<String> skuCodes) {
        logger.info("Calling Product Service to fetch metadata batch for SKUs: {}", skuCodes);

        return webClientBuilder.build()
                .post()
                .uri(PRODUCT_SERVICE_URL)
                .bodyValue(skuCodes)
                .retrieve()
                .bodyToFlux(ProductMetadata.class)
                .collectList()
                .block();
    }

}
