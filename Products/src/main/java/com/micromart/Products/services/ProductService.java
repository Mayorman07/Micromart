package com.micromart.products.services;
import com.micromart.products.model.data.ProductDto;
import com.micromart.products.model.responses.CreateProductResponse;
import com.micromart.products.model.responses.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto createProductDetails);
    ProductDto updateProduct(ProductDto updateProductDetails);
    void deleteProduct(Long id);
   ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(Long categoryId);
    List<ProductResponse> searchProducts(String keyword);
}
