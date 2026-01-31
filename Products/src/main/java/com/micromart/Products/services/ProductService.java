package com.micromart.products.services;
import com.micromart.products.model.data.ProductDto;
import com.micromart.products.model.responses.CreateProductResponse;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto createProductDetails);
    ProductDto updateProduct(String id, ProductDto updateProductDetails);
    void deleteProduct(String id);
    CreateProductResponse getProductById(String id);
    List<CreateProductResponse> getAllProducts();
    List<CreateProductResponse> getProductsByCategory(Long categoryId);
    List<CreateProductResponse> searchProducts(String keyword);
}
