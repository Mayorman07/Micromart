package com.micromart.Inventory.repository;

import com.micromart.Inventory.entity.Inventory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findBySkuCodeIn(List<String> skuCodes);
    Optional<Inventory> findBySkuCode( String SkuCode);

    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity - :qty WHERE i.skuCode = :sku AND i.quantity >= :qty")
    int deductStock(@Param("sku") String sku, @Param("qty") Integer qty);

}
