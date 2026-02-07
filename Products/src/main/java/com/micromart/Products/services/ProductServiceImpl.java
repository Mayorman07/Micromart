package com.micromart.products.services;

import com.micromart.products.model.data.ProductDto;
import com.micromart.products.model.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public ProductDto createProduct(ProductDto createProductDetails) {
        return null;
    }

    @Override
    public ProductDto updateProduct(ProductDto updateProductDetails) {
        return null;
    }


    @Override
    public void deleteProduct(Long id) {

    }

    @Override
    public ProductResponse getProductById(Long id) {
        return null;
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return null;
    }


    @Override
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return null;
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        return null;
    }
}
