package com.example.digitallogistics.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    List<Inventory> findByWarehouseId(UUID id);
    List<Inventory> findByProductId(UUID id);
    java.util.Optional<Inventory> findByWarehouseIdAndProductId(UUID warehouseId, UUID productId);
}
