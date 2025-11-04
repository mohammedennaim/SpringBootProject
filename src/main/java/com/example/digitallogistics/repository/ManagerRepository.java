package com.example.digitallogistics.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.digitallogistics.model.entity.Manager;

public interface ManagerRepository extends JpaRepository<Manager, UUID> {
    
    // Trouver les managers qui gèrent un entrepôt spécifique
    @Query("SELECT m FROM Manager m JOIN m.warehouses w WHERE w.id = :warehouseId")
    List<Manager> findByWarehouseId(@Param("warehouseId") UUID warehouseId);
    
    // Trouver les managers actifs
    List<Manager> findByActiveTrue();
    
    // Trouver un manager par email
    Optional<Manager> findByEmail(String email);
    
    // Trouver les managers qui n'ont aucun entrepôt assigné
    @Query("SELECT m FROM Manager m WHERE m.warehouses IS EMPTY")
    List<Manager> findManagersWithoutWarehouses();
}
