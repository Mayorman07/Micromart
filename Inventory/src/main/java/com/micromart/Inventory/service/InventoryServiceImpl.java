package com.micromart.Inventory.service;

import com.micromart.Inventory.entity.Inventory;
import com.micromart.Inventory.model.data.InventoryDto;
import com.micromart.Inventory.model.responses.InventoryResponse;
import com.micromart.Inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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

    @Override
    public InventoryDto addStock(InventoryDto inventoryDto) {
        Inventory inventory = inventoryRepository.findBySkuCode(inventoryDto.getSkuCode())
                .orElseThrow(() -> new RuntimeException("Product not found in inventory: " + inventoryDto.getSkuCode()));

        inventory.setQuantity(inventory.getQuantity() + inventoryDto.getQuantity());
        inventoryRepository.save(inventory);
        return modelMapper.map(inventory, InventoryDto.class);
    }


    @Override
    @Transactional
    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public InventoryDto deductStock(InventoryDto inventoryDto) {
        Inventory inventory = inventoryRepository.findBySkuCode(inventoryDto.getSkuCode())
                .orElseThrow(() -> new RuntimeException("Product not found in inventory: " + inventoryDto.getSkuCode()));

        if (inventory.getQuantity() < inventoryDto.getQuantity()) {
            throw new RuntimeException("Insufficient stock for SKU: " + inventoryDto.getSkuCode());
        }

        inventory.setQuantity(inventory.getQuantity() - inventoryDto.getQuantity());
        inventoryRepository.save(inventory);

        return modelMapper.map(inventory, InventoryDto.class);
    }

}
