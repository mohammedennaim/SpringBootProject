package com.example.digitallogistics.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.digitallogistics.model.entity.Manager;

public interface ManagerRepository extends JpaRepository<Manager, UUID> {
    List<Manager> findByWarehouseId(UUID warehouseId);
    List<Manager> findByActiveTrue();
    Optional<Manager> findByEmail(String email);
}
