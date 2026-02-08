package com.micromart.products.controllers;
import com.micromart.products.model.data.CategoryDto;
import com.micromart.products.model.requests.CreateCategoryRequest;
import com.micromart.products.model.responses.CategoryResponse;
import com.micromart.products.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @PostMapping(path="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFILE_CREATE')")
    public ResponseEntity<CategoryResponse> createCategory (@Valid @RequestBody CreateCategoryRequest createCategoryRequest){
        logger.info("The incoming create category request {} " , createCategoryRequest);
        CategoryDto createCategory = modelMapper.map(createCategoryRequest,CategoryDto.class);
        CategoryResponse createdCategory = categoryService.createCategory(createCategory);
        CategoryResponse returnValue = modelMapper.map(createdCategory,CategoryResponse.class);
        logger.info("The out going create category response {} " , returnValue);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
    @PutMapping(path="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFILE_UPDATE')")
    public ResponseEntity<CategoryResponse> updateCategory( @PathVariable Long id,@Valid @RequestBody CreateCategoryRequest updateCategoryRequest){
        CategoryDto updateCategory = modelMapper.map(updateCategoryRequest,CategoryDto.class);
        CategoryResponse updatedCategory = categoryService.updateCategory(id,updateCategory);
        CategoryResponse returnValue = modelMapper.map(updateCategory, CategoryResponse.class);
        logger.info("The out going update category response {} " , returnValue);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
    @DeleteMapping(path="/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFILE_DELETE')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping(path="/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    @GetMapping(path="/category,{name}")
    public ResponseEntity<CategoryResponse> getCategoryByName(@PathVariable String name){
        CategoryResponse category = categoryService.getCategoryByName(name);
        return ResponseEntity.ok(modelMapper.map(category,CategoryResponse.class));
    }
    @GetMapping("/categoryId,{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id){
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(modelMapper.map(category,CategoryResponse.class));
    }

}
