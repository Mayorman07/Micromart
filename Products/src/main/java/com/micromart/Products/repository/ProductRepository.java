package com.micromart.products.repository;

import com.micromart.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Check if SKU exists (for validation)
    boolean existsBySku(String sku);

    // Find a product by SKU (for updates/fetching)
    Optional<Product> findBySku(String sku);

    // Simple search: Find products where name or description contains the text (case insensitive)
    // Example: findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("phone", "phone")
    // Note: For a real production app, we would use a proper search engine or @Query,
    // but this is perfect for now.
    Iterable<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}
