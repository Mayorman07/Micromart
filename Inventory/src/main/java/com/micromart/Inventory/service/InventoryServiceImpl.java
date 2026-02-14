package com.micromart.Inventory.service;

import com.micromart.Inventory.entity.Inventory;
import com.micromart.Inventory.model.data.InventoryDto;
import com.micromart.Inventory.model.responses.InventoryResponse;
import com.micromart.Inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    @Override
    @Transactional
    public InventoryDto createInventory(InventoryDto inventoryDto) {
        Inventory inventory = inventoryRepository.findBySkuCode(inventoryDto.getSkuCode())
                .orElseGet(() -> {
                    Inventory newInventory = new Inventory();
                    newInventory.setSkuCode(inventoryDto.getSkuCode());
                    newInventory.setQuantity(0);
                    return newInventory;
                });

        inventory.setQuantity(inventory.getQuantity() + inventoryDto.getQuantity());
        Inventory savedInventory = inventoryRepository.save(inventory);

        return modelMapper.map(savedInventory, InventoryDto.class);
    }


    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();
    }
}
