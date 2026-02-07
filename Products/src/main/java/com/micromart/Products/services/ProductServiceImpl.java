package com.micromart.products.services;

import com.micromart.products.entity.Product;
import com.micromart.products.exceptions.AlreadyExistsException;
import com.micromart.products.exceptions.ResourceNotFoundException;
import com.micromart.products.model.data.ProductDto;
import com.micromart.products.model.responses.ProductResponse;
import com.micromart.products.repository.ProductRepository;
import com.micromart.products.utils.SkuCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final SkuCodeGenerator skuCodeGenerator;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    @Transactional
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
    public ProductDto updateProduct(Long id, ProductDto updateProductDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        modelMapper.map(updateProductDetails, existingProduct);
        Product productToBeUpdated = productRepository.save(existingProduct);
        return modelMapper.map(productToBeUpdated,ProductDto.class);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(product -> modelMapper.map(product, ProductResponse.class));
    }
    @Override
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryId(categoryId,pageable);
        return productPage.map(product -> modelMapper.map(product, ProductResponse.class));
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        Page<Product> productPage = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pageable);

        return productPage.map(product -> modelMapper.map(product, ProductResponse.class));
    }

}
