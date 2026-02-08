package com.micromart.products.services;

import com.micromart.products.model.data.CategoryDto;
import com.micromart.products.model.responses.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse getCategoryById(Long id);
    CategoryResponse getCategoryByName(String name);
    List<CategoryResponse> getAllCategories();
    CategoryResponse createCategory(CategoryDto createCategoryDetails);
    CategoryResponse updateCategory(Long id, CategoryDto categoryDto);
    void deleteCategory(Long id);
}
