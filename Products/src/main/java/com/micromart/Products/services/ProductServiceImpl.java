package com.micromart.products.services;

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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductServiceImpl implements ProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;
    private final SkuCodeGenerator skuCodeGenerator;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository,
                              CategoryRepository categoryRepository, SkuCodeGenerator skuCodeGenerator) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.categoryRepository=categoryRepository;
        this.skuCodeGenerator=skuCodeGenerator;
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto createProductDetails) {

        Category category = categoryRepository.findById(createProductDetails.getCategoryId())
                .orElseThrow(() -> new NotFoundException(
                        "Cannot create product. Category not found with ID: " + createProductDetails.getCategoryId()
                ));

        String generatedSku = skuCodeGenerator.generateSku(
                createProductDetails.getBrand(),
                createProductDetails.getName(),
                createProductDetails.getVariant()
        );

        if (productRepository.existsBySku(generatedSku)) {
            throw new AlreadyExistsException("Product with SKU " + generatedSku + " already exists");
        }
        Product productToBeCreated = modelMapper.map(createProductDetails, Product.class);
        productToBeCreated.setId(null);
        productToBeCreated.setSku(generatedSku);
        productToBeCreated.setCategory(category);

        category.addProduct(productToBeCreated);
        Product createdProduct = productRepository.save(productToBeCreated);
        return modelMapper.map(createdProduct, ProductDto.class);
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
