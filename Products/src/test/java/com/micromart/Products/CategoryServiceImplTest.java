package com.micromart.Products;


import com.micromart.products.entity.Category;
import com.micromart.products.entity.Product;
import com.micromart.products.exceptions.AlreadyExistsException;
import com.micromart.products.model.data.CategoryDto;
import com.micromart.products.model.responses.CategoryResponse;
import com.micromart.products.repository.CategoryRepository;
import com.micromart.products.services.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock private ModelMapper modelMapper;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryDto categoryDto;
    private Category category;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto();
        categoryDto.setName("Electronics");
        categoryDto.setDescription("Tech gadgets");

        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setProducts(new ArrayList<>());

        categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("Electronics");
    }

    @Test
    @DisplayName("createCategory - Success: Saves non-existent category")
    void createCategory_Success() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(modelMapper.map(category, CategoryResponse.class)).thenReturn(categoryResponse);

        CategoryResponse result = categoryService.createCategory(categoryDto);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("createCategory - Failure: Throws exception if name exists")
    void createCategory_Exists_ThrowsException() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> categoryService.createCategory(categoryDto));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteCategory - Success: Deletes category with no products")
    void deleteCategory_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    @DisplayName("deleteCategory - Failure: Throws IllegalStateException if products exist")
    void deleteCategory_HasProducts_ThrowsException() {
        // Add a mock product to the category to trigger the guard clause
        category.getProducts().add(new Product());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(IllegalStateException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).delete(any());
    }
}
