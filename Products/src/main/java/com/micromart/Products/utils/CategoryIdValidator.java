package com.micromart.products.utils;

import com.micromart.products.entity.Category;
import com.micromart.products.exceptions.NotFoundException;
import com.micromart.products.repository.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class CategoryIdValidator {

    CategoryRepository categoryRepository;

    public Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + categoryId));
    }
}
