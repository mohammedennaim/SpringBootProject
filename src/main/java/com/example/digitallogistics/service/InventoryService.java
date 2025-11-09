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
    void adjustProductTotal(java.util.UUID productId, int targetTotal);
    /**
     * Update an existing inventory by id. Will set qtyOnHand and qtyReserved and can change warehouse/product references.
     */
    Inventory updateInventory(UUID id, UUID warehouseId, UUID productId, Integer qtyOnHand, Integer qtyReserved);
}