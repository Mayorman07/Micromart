package com.micromart.products.services;

import com.micromart.products.client.InventoryClient;
import com.micromart.products.entity.Category;
import com.micromart.products.entity.Product;
import com.micromart.products.exceptions.AlreadyExistsException;
import com.micromart.products.exceptions.NotFoundException;
import com.micromart.products.exceptions.ResourceNotFoundException;
import com.micromart.products.model.data.ProductDto;
import com.micromart.products.model.responses.ProductResponse;
import com.micromart.products.repository.CategoryRepository;
import com.micromart.products.repository.ProductRepository;
import com.micromart.products.utils.SkuCodeGenerator;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class ProductServiceImpl implements ProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SkuCodeGenerator skuCodeGenerator;
    private final InventoryClient inventoryClient;


    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository,
                              CategoryRepository categoryRepository, SkuCodeGenerator skuCodeGenerator, WebClient.Builder webClientBuilder, InventoryClient inventoryClient) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.categoryRepository=categoryRepository;
        this.skuCodeGenerator=skuCodeGenerator;
        this.inventoryClient = inventoryClient;
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto createProductDetails) {

        Category category = getCategoryOrThrow(createProductDetails.getCategoryId());
        String skuCode = resolveSkuCode(createProductDetails);

        if (productRepository.existsBySkuCode(skuCode)) {
            throw new AlreadyExistsException("Product with SKU " + skuCode + " already exists");
        }

        Product product = new Product();
        product.setName(createProductDetails.getName());
        product.setDescription(createProductDetails.getDescription());
        product.setPrice(createProductDetails.getPrice());
        product.setSkuCode(skuCode);
        product.setImageUrl(createProductDetails.getImageUrl());
        product.setActive(true);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        logger.info("Product saved to Catalog DB. ID: {}", savedProduct.getId());
        inventoryClient.initializeStock(skuCode, createProductDetails.getStockQuantity());

        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto updateProductDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.setName(updateProductDetails.getName());
        existingProduct.setDescription(updateProductDetails.getDescription());
        existingProduct.setPrice(updateProductDetails.getPrice());
        existingProduct.setImageUrl(updateProductDetails.getImageUrl());
        existingProduct.setCategory(getCategoryOrThrow(updateProductDetails.getCategoryId()));
        Product productToBeUpdated = productRepository.save(existingProduct);
        return modelMapper.map(productToBeUpdated, ProductDto.class);
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

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + categoryId));
    }

    private String resolveSkuCode(ProductDto request) {
        if (request.getSkuCode() != null && !request.getSkuCode().isEmpty()) {
            return request.getSkuCode();
        }
        return skuCodeGenerator.generateSku(request.getBrand(), request.getName(), request.getVariant());
    }

}
