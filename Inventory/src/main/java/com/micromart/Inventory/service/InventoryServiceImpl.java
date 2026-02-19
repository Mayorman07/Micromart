package com.micromart.Inventory.service;

import com.micromart.Inventory.client.ProductClient;
import com.micromart.Inventory.entity.Inventory;
import com.micromart.Inventory.exceptions.NotFoundException;
import com.micromart.Inventory.model.data.InventoryDto;
import com.micromart.Inventory.model.meta.ProductMetadata;
import com.micromart.Inventory.model.responses.InventoryResponse;
import com.micromart.Inventory.repository.InventoryRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService{
    private final InventoryRepository inventoryRepository;

    private final ProductClient productClient;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    public InventoryServiceImpl(InventoryRepository inventoryRepository, ProductClient productClient, ModelMapper modelMapper) {
        this.inventoryRepository = inventoryRepository;
        this.productClient = productClient;
        this.modelMapper = modelMapper;
    }

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
                .map(inventory -> {
                    InventoryResponse response = new InventoryResponse();

                    response.setSkuCode(inventory.getSkuCode());
                    response.setInStock(inventory.getQuantity() > 0);
                     response.setQuantity(inventory.getQuantity());

                    return response;
                }).toList();
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
                .orElseThrow(() -> new NotFoundException("Product not found in inventory: " + inventoryDto.getSkuCode()));

        if (inventory.getQuantity() < inventoryDto.getQuantity()) {
            throw new NotFoundException("Insufficient stock for SKU: " + inventoryDto.getSkuCode());
        }

        inventory.setQuantity(inventory.getQuantity() - inventoryDto.getQuantity());
        inventoryRepository.save(inventory);

        return modelMapper.map(inventory, InventoryDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryResponse> getAggregatedInventory(Pageable pageable, String keyword) {
        Page<Inventory> inventoryPage;
        if (keyword != null && !keyword.isEmpty()) {
            inventoryPage = inventoryRepository.findBySkuCodeContainingIgnoreCase(keyword, pageable);
        } else {
            inventoryPage = inventoryRepository.findAll(pageable);
        }

        return inventoryPage.map(inv -> {
            ProductMetadata meta = productClient.getProductMetadata(inv.getSkuCode());

            InventoryResponse response = new InventoryResponse();

            response.setSkuCode(inv.getSkuCode());
            response.setQuantity(inv.getQuantity());
            response.setInStock(inv.getQuantity() > 0);

            if (meta != null) {
                response.setName(meta.getName());
                response.setPrice(meta.getPrice());
                response.setCategory(meta.getCategory());
            } else {
                response.setName("OPERATIVE_UNKNOWN_SKU");
            }
            return response;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryResponse> getInventoryRegistry(Pageable pageable) {
        return getAggregatedInventory(pageable, null);
    }

}
