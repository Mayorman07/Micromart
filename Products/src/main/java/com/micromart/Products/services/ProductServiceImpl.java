package com.micromart.products.services;

import com.micromart.products.client.InventoryClient;
import com.micromart.products.entity.Category;
import com.micromart.products.entity.Product;
import com.micromart.products.exceptions.AlreadyExistsException;
import com.micromart.products.exceptions.NotFoundException;
import com.micromart.products.exceptions.ResourceNotFoundException;
import com.micromart.products.model.data.ProductDto;
import com.micromart.products.model.meta.ProductMetadata;
import com.micromart.products.model.responses.ProductResponse;
import com.micromart.products.repository.ProductRepository;
import com.micromart.products.utils.CategoryIdValidator;
import com.micromart.products.utils.SkuCodeGenerator;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductServiceImpl implements ProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final SkuCodeGenerator skuCodeGenerator;
    private final InventoryClient inventoryClient;
    private final CategoryIdValidator categoryIdValidator;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository, SkuCodeGenerator skuCodeGenerator, WebClient.Builder webClientBuilder,
                              InventoryClient inventoryClient,CategoryIdValidator categoryIdValidator) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.skuCodeGenerator=skuCodeGenerator;
        this.inventoryClient = inventoryClient;
        this.categoryIdValidator=categoryIdValidator;
    }

    @Override
    public ProductDto createProduct(ProductDto createProductDetails) {
        Product savedProduct = null;
        String skuCode = resolveSkuCode(createProductDetails);

        try {
            savedProduct = saveProductToDatabase(createProductDetails, skuCode);
            inventoryClient.initializeStock(skuCode, createProductDetails.getStockQuantity());
            return modelMapper.map(savedProduct, ProductDto.class);

        } catch (Exception e) {
            logger.error("Stock initialization failed. Compensating by deleting product.", e);
            if (savedProduct != null) {
                compensateDeleteProduct(savedProduct.getId());
            }
            throw new NotFoundException("Failed to create product: " + e.getMessage());
        }
    }
    @Transactional
    public Product saveProductToDatabase(ProductDto details, String skuCode) {
        if (productRepository.existsBySkuCode(skuCode)) {
            throw new AlreadyExistsException("Product with SKU " + skuCode + " already exists");
        }

        Category category = categoryIdValidator.getCategoryOrThrow(details.getCategoryId());
        Product product = new Product();
        product.setName(details.getName());
        product.setDescription(details.getDescription());
        product.setPrice(details.getPrice());
        product.setImageUrl(details.getImageUrl());
        product.setSkuCode(skuCode);
        product.setActive(true);
        product.setCategory(category);
        return productRepository.save(product);
    }

    @Transactional
    public void compensateDeleteProduct(Long productId) {
        try {
            productRepository.deleteById(productId);
            logger.info("Compensation successful: Product {} deleted", productId);
        } catch (Exception e) {
            logger.error("COMPENSATION FAILED! Product {} exists without stock.", productId, e);
        }
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto updateProductDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.setName(updateProductDetails.getName());
        existingProduct.setDescription(updateProductDetails.getDescription());
        existingProduct.setPrice(updateProductDetails.getPrice());
        existingProduct.setImageUrl(updateProductDetails.getImageUrl());
        existingProduct.setCategory(categoryIdValidator.getCategoryOrThrow(updateProductDetails.getCategoryId()));
        Product productToBeUpdated = productRepository.save(existingProduct);
        return modelMapper.map(productToBeUpdated, ProductDto.class);
    }

    @Override
    public ProductMetadata getMetadataBySku(String sku) {
        Product product = productRepository.findBySkuCode(sku)
                .orElseThrow(() -> new RuntimeException("Product not found for SKU: " + sku));

        ProductMetadata metadata = new ProductMetadata();
        metadata.setName(product.getName());
        metadata.setPrice(product.getPrice());

        if (product.getCategory() != null) {
            metadata.setCategory(product.getCategory().getName());
        }

        return metadata;
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

    public List<ProductMetadata> getMetadataForSkus(List<String> skuCodes) {
        return productRepository.findBySkuCodeIn(skuCodes)
                .stream()
                .map(product -> new ProductMetadata(
                        product.getName(),
                        product.getPrice(),
                        product.getCategory().getName(),
                        product.getSkuCode(),
                        product.getImageUrl()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);

        return productPage.map(product -> {
            ProductResponse response = modelMapper.map(product, ProductResponse.class);

            if (product.getCategory() != null) {
                response.setCategoryId(product.getCategory().getId());
                response.setCategoryName(product.getCategory().getName());
            }

            return response;
        });
    }

    @Override
    public ProductResponse getProductBySku(String skuCode) {
        Product product = productRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + skuCode));
        return modelMapper.map(product, ProductResponse.class);
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


    private String resolveSkuCode(ProductDto request) {
        if (request.getSkuCode() != null && !request.getSkuCode().isEmpty()) {
            return request.getSkuCode();
        }
        return skuCodeGenerator.generateSku(request.getBrand(), request.getName(), request.getVariant());
    }

}
