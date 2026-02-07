package com.micromart.products.controllers;

import com.micromart.products.model.data.ProductDto;
import com.micromart.products.model.requests.CreateProductRequest;
import com.micromart.products.model.responses.ProductResponse;
import com.micromart.products.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest; // If you need to create a manual request
import org.springframework.data.web.PageableDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @GetMapping(path = "/test/status")
    public String status(){

        return "Just testing as usual";
    }
    @PostMapping(path ="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFILE_CREATE')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest productRequest){
        logger.info("The incoming create product request {} " , productRequest);
        ProductDto productDto = modelMapper.map(productRequest,ProductDto.class);
        ProductDto createdProductDto = productService.createProduct(productDto);
        ProductResponse returnValue = modelMapper.map(createdProductDto, ProductResponse.class);
        logger.info("The out going create product response {} " , returnValue);
        return  ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
    @PostMapping(path ="/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFILE_UPDATE')")
    public ResponseEntity<ProductResponse>  updateProduct(@Valid @RequestBody CreateProductRequest productRequest){
        logger.info("The incoming update product request {} " , productRequest);
        ProductDto productDto = modelMapper.map(productRequest,ProductDto.class);
        ProductDto updateProductRequest = productService.updateProduct(productDto);
        ProductResponse returnValue = modelMapper.map(updateProductRequest, ProductResponse.class);
        logger.info("The out going update product response {} " , returnValue);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFILE_DELETE')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping(path = "/view/{id}")
    @PreAuthorize("hasAuthority('product:READ')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse productResponse = productService.getProductById(id);
        return ResponseEntity.ok(productResponse);
    }
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(@PageableDefault(page = 0, size = 15) Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }
    @GetMapping(path = "/category/{id}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(@PathVariable Long categoryId, Pageable pageable){
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }
}
