package com.micromart.products.services;
import com.micromart.products.model.data.ProductDto;
import com.micromart.products.model.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto createProductDetails);
    ProductDto updateProduct(ProductDto updateProductDetails);
    void deleteProduct(Long id);
   ProductResponse getProductById(Long id);
    Page<ProductResponse> getAllProducts(Pageable pageable);
    List<ProductResponse> getProductsByCategory(Long categoryId);
    List<ProductResponse> searchProducts(String keyword);
}
