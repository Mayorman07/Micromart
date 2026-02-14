package com.micromart.Inventory.service;

import com.micromart.Inventory.model.data.InventoryDto;
import com.micromart.Inventory.model.responses.InventoryResponse;

import java.util.List;

public interface InventoryService {

    InventoryDto createInventory(InventoryDto inventoryDto);
    List<InventoryResponse> isInStock(List<String> skuCodes);

}
