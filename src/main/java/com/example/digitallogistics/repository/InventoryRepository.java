package com.example.digitallogistics.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByWarehouseId(Long id);
    List<Inventory> findByProductId(Long id);
}
