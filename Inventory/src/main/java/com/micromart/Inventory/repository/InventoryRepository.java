package com.micromart.Inventory.repository;

import com.micromart.Inventory.entity.Inventory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findBySkuCodeIn(List<String> skuCodes);
    Optional<Inventory> findBySkuCode( String SkuCode);

}
