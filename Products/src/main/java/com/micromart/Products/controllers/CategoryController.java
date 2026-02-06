package com.micromart.products.controllers;
import com.micromart.products.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    public void getAllCategories(){

    }

    public void createCategory (){

    }

}
