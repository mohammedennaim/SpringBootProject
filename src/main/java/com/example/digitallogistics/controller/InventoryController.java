package com.example.digitallogistics.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.model.dto.InventoryAdjustDto;
import com.example.digitallogistics.model.dto.InventoryDto;
import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.mapper.InventoryMapper;
import com.example.digitallogistics.service.InventoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    public InventoryController(InventoryService inventoryService, InventoryMapper inventoryMapper) {
        this.inventoryService = inventoryService;
        this.inventoryMapper = inventoryMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public List<InventoryDto> list(@RequestParam(required = false) UUID warehouseId,
                                   @RequestParam(required = false) UUID productId) {
        List<Inventory> inventories;
        
        if (warehouseId != null) {
            inventories = inventoryService.findByWarehouseId(warehouseId);
        } else if (productId != null) {
            inventories = inventoryService.findByProductId(productId);
        } else {
            inventories = inventoryService.findAll();
        }
        
        return inventories.stream()
                .map(inventoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<InventoryDto> get(@PathVariable UUID id) {
        Optional<Inventory> inventory = inventoryService.findById(id);
        if (inventory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(inventoryMapper.toDto(inventory.get()));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<InventoryDto> adjustInventory(@RequestBody @Valid InventoryAdjustDto adjustDto) {
        try {
            Inventory adjustedInventory = inventoryService.adjustInventory(
                adjustDto.getWarehouseId(),
                adjustDto.getProductId(),
                adjustDto.getAdjustmentQty(),
                adjustDto.getReason()
            );
            return ResponseEntity.ok(inventoryMapper.toDto(adjustedInventory));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{productId}/available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or hasRole('CLIENT')")
    public ResponseEntity<Integer> getAvailableQuantity(@PathVariable UUID productId,
                                                       @RequestParam(required = false) UUID warehouseId) {
        Integer availableQty;
        
        if (warehouseId != null) {
            availableQty = inventoryService.getAvailableQuantityInWarehouse(warehouseId, productId);
        } else {
            availableQty = inventoryService.getAvailableQuantity(productId);
        }
        
        return ResponseEntity.ok(availableQty);
    }
}