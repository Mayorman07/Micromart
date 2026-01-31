package com.micromart.products.services;
import com.micromart.products.model.requests.ProductRequest;
import com.micromart.products.model.responses.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse updateProduct(String id, ProductRequest productRequest);
    void deleteProduct(String id);
    ProductResponse getProductById(String id);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(Long categoryId);
    List<ProductResponse> searchProducts(String keyword);
}
