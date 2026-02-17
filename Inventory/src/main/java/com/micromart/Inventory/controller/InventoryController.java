package com.micromart.Inventory.controller;

import com.micromart.Inventory.model.data.InventoryDto;
import com.micromart.Inventory.model.requests.InventoryRequest;
import com.micromart.Inventory.model.responses.InventoryResponse;
import com.micromart.Inventory.service.InventoryService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    public InventoryController(InventoryService inventoryService, ModelMapper modelMapper) {
        this.inventoryService = inventoryService;
        this.modelMapper = modelMapper;
    }
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
    @PutMapping("/add")
    public ResponseEntity<InventoryResponse>  addStock(@RequestBody InventoryRequest inventoryRequest){
        InventoryDto addStock = modelMapper.map(inventoryRequest, InventoryDto.class);
        InventoryDto stockToBeAdded = inventoryService.addStock(addStock);
        InventoryResponse returnValue = modelMapper.map(stockToBeAdded, InventoryResponse.class);
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }
    @PutMapping("/deduct")
    public ResponseEntity<InventoryResponse> deductStock(@RequestBody InventoryRequest inventoryRequest){
        InventoryDto deductStock = modelMapper.map(inventoryRequest, InventoryDto.class);
        InventoryDto stockToBeDeducted = inventoryService.deductStock(deductStock);
        InventoryResponse returnValue = modelMapper.map(stockToBeDeducted, InventoryResponse.class);
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InventoryResponse>> getFullInventory(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(size = 15) Pageable pageable) {

        return ResponseEntity.ok(inventoryService.getAggregatedInventory(pageable, keyword));
    }
}
