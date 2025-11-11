package com.example.digitallogistics.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.digitallogistics.model.entity.Manager;

public interface ManagerRepository extends JpaRepository<Manager, UUID> {
    @Query("SELECT m FROM Manager m JOIN m.warehouses w WHERE w.id = :warehouseId")
    List<Manager> findByWarehouseId(@Param("warehouseId") UUID warehouseId);
    
    List<Manager> findByActiveTrue();
    
    Optional<Manager> findByEmail(String email);
    
    @Query("SELECT m FROM Manager m WHERE m.warehouses IS EMPTY")
    List<Manager> findManagersWithoutWarehouses();
}
