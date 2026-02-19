package com.micromart.products.repository;

import com.micromart.products.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySkuCode(String skuCode);
    boolean existsByNameIgnoreCase(String name);
    Optional<Product> findBySkuCode(String skuCode);
    List<Product> findBySkuCodeIn(List<String> skuCodes);
   Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name,
            String description,
            Pageable pageable
    );}
