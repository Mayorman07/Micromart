package com.micromart.products.controllers;

import com.micromart.products.model.data.ProductDto;
import com.micromart.products.model.requests.CreateProductRequest;
import com.micromart.products.model.responses.CreateProductResponse;
import com.micromart.products.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @GetMapping(path = "/test/status")
    public String status(){

        return "Just testing as usual, normal normal";
    }
    @PostMapping(path ="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateProductResponse> createProduct(@Valid @RequestBody CreateProductRequest productRequest){
        logger.info("The incoming create product request {} " , productRequest);
        ProductDto productDto = modelMapper.map(productRequest,ProductDto.class);
        ProductDto createdProductDto = productService.createProduct(productDto);
        CreateProductResponse returnValue = modelMapper.map(createdProductDto, CreateProductResponse.class);
        logger.info("The out going create product response {} " , returnValue);
        return  ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
    @PostMapping(path ="/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateProduct(@Valid @RequestBody CreateProductRequest productRequest){
        logger.info("The incoming update product request {} " , productRequest);
    }

}
