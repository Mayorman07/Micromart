package com.micromart.products.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto implements Serializable {
    @Serial
    private  static final long serialVersionUID = -953297098295050686L;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private String categoryId;
    private String description;
    private String imageUrl;
    private String categoryName;
    private String skuCode;
}
