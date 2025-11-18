package com.example.digitallogistics.repository;

import com.example.digitallogistics.model.entity.BackOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BackOrderRepository extends JpaRepository<BackOrder, UUID> {
    
    List<BackOrder> findByIsFulfilledFalse();
    
    List<BackOrder> findByProductIdAndWarehouseIdAndIsFulfilledFalse(UUID productId, UUID warehouseId);
}