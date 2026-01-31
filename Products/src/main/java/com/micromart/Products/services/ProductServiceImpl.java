package com.micromart.products.services;

import com.micromart.products.model.data.ProductDto;
import com.micromart.products.model.responses.CreateProductResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public ProductDto createProduct(ProductDto createProductDetails) {
        return null;
    }

    @Override
    public ProductDto updateProduct(String id, ProductDto updateProductDetails) {
        return null;
    }

    @Override
    public void deleteProduct(String id) {

    }

    @Override
    public CreateProductResponse getProductById(String id) {
        return null;
    }

    @Override
    public List<CreateProductResponse> getAllProducts() {
        return null;
    }

    @Override
    public List<CreateProductResponse> getProductsByCategory(Long categoryId) {
        return null;
    }

    @Override
    public List<CreateProductResponse> searchProducts(String keyword) {
        return null;
    }
}
