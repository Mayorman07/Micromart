package com.micromart.products.services;

import com.micromart.products.entity.Product;
import com.micromart.products.exceptions.AlreadyExistsException;
import com.micromart.products.model.data.ProductDto;
import com.micromart.products.model.responses.ProductResponse;
import com.micromart.products.repository.ProductRepository;
import com.micromart.products.utils.SkuCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final SkuCodeGenerator skuCodeGenerator;
    @Override
    public ProductDto createProduct(ProductDto createProductDetails) {
        String generatedSku = skuCodeGenerator.generateSku(
                createProductDetails.getBrand(),
                createProductDetails.getName(),
                createProductDetails.getVariant()
        );
        if(productRepository.existsBySku(generatedSku))
            throw new AlreadyExistsException("Product with SKU " + createProductDetails.getSkuCode() + " already exists");
       createProductDetails.setSkuCode(generatedSku);
        Product productToBeCreated =modelMapper.map(createProductDetails, Product.class);
        Product createdProduct = productRepository.save(productToBeCreated);
        return modelMapper.map(createdProduct,ProductDto.class);
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
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return null;
    }


}
