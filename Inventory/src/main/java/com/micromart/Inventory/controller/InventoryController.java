package com.micromart.Inventory.controller;

import com.micromart.Inventory.model.data.InventoryDto;
import com.micromart.Inventory.model.requests.InventoryRequest;
import com.micromart.Inventory.model.responses.InventoryResponse;
import com.micromart.Inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;
    @PostMapping(path ="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryResponse> createInventory(@RequestBody InventoryRequest inventoryRequest){
        InventoryDto createInventory = modelMapper.map(inventoryRequest,InventoryDto.class);
        InventoryDto createdInventory = inventoryService.createInventory(createInventory);
        InventoryResponse returnValue = modelMapper.map(createdInventory, InventoryResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
    @GetMapping("/sku-code/{sku-code}")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@PathVariable("sku-code") List<String> skuCodes) {
        return inventoryService.isInStock(skuCodes);
    }
}
