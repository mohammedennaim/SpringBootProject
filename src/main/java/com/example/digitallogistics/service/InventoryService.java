package com.example.digitallogistics.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.digitallogistics.model.entity.Inventory;

public interface InventoryService {
    List<Inventory> findAll();
    List<Inventory> findByWarehouseId(UUID warehouseId);
    List<Inventory> findByProductId(UUID productId);
    Optional<Inventory> findById(UUID id);
    Optional<Inventory> findByWarehouseAndProduct(UUID warehouseId, UUID productId);
    Inventory adjustInventory(UUID warehouseId, UUID productId, Integer adjustmentQty, String reason);
    Integer getAvailableQuantity(UUID productId);
    Integer getAvailableQuantityInWarehouse(UUID warehouseId, UUID productId);
    /**
     * Adjust inventories so that the sum of qty_on_hand for the given product equals targetTotal.
     * If targetTotal is greater than current sum, will add the difference to the MAIN warehouse inventory
     * (creating it if necessary). If targetTotal is smaller, will remove quantity from inventories
     * preferring those with highest available on-hand. This method will not reduce below reserved quantities.
     */
    void adjustProductTotal(java.util.UUID productId, int targetTotal);
}