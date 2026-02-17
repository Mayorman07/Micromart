package com.micromart.Inventory.service;

import com.micromart.Inventory.model.data.InventoryDto;
import com.micromart.Inventory.model.responses.InventoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryService {

    InventoryDto createInventory(InventoryDto inventoryDto);
    List<InventoryResponse> isInStock(List<String> skuCodes);
    Page<InventoryResponse> getAggregatedInventory(Pageable pageable, String keyword);
    Page<InventoryResponse> getInventoryRegistry(Pageable pageable);
    InventoryDto addStock(InventoryDto inventoryDto);
    InventoryDto deductStock(InventoryDto inventoryDto);

}
