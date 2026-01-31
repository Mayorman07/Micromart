package com.micromart.products.model.responses;

import lombok.Data;

@Data
public class CreateCategoryResponse {
    private Long id;
    private String name;
    private String description;
}
