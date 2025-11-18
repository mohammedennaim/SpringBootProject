package com.example.digitallogistics.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.digitallogistics.model.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    List<Inventory> findByWarehouseId(UUID id);
    List<Inventory> findByProductId(UUID id);
    Optional<Inventory> findByWarehouseIdAndProductId(UUID warehouseId, UUID productId);
    
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId ORDER BY i.warehouse.priority ASC")
    List<Inventory> findByProductIdOrderByWarehousePriority(@Param("productId") UUID productId);
    
    Optional<Inventory> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId);
}
