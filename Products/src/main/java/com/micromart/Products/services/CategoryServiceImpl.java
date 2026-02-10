package com.micromart.products.services;

import com.micromart.products.entity.Category;
import com.micromart.products.entity.Product;
import com.micromart.products.exceptions.AlreadyExistsException;
import com.micromart.products.exceptions.NotFoundException;
import com.micromart.products.exceptions.ResourceNotFoundException;
import com.micromart.products.model.data.CategoryDto;
import com.micromart.products.model.responses.CategoryResponse;
import com.micromart.products.model.responses.ProductResponse;
import com.micromart.products.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService{
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(ModelMapper modelMapper, CategoryRepository categoryRepository) {
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryDto createCategoryDetails) {
        if (categoryRepository.existsByName(createCategoryDetails.getName())){
            throw new AlreadyExistsException("Category already exists");
        }
        Category createdCategory = modelMapper.map(createCategoryDetails,Category.class);
        Category savedCategory = categoryRepository.save(createdCategory);
        return modelMapper.map(savedCategory, CategoryResponse.class);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryDto updateCategoryDetails) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category id is invalid"));
        existingCategory.setName(updateCategoryDetails.getName());
        existingCategory.setDescription(updateCategoryDetails.getDescription());
        Category savedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(savedCategory, CategoryResponse.class);
    }
    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public CategoryResponse getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
        .orElseThrow(() -> new NotFoundException("Category with name " + name + "not found: "));
        return modelMapper.map(category,CategoryResponse.class);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> allCategories = categoryRepository.findAll();
        return allCategories.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category id is invalid"));
        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with existing products. Please move them first.");
        }
        categoryRepository.delete(category);
    }

}
