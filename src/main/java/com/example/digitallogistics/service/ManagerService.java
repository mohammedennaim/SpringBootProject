package com.example.digitallogistics.service;

import com.example.digitallogistics.model.entity.Manager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManagerService {
    List<Manager> findAll();
    Optional<Manager> findById(UUID id);
    List<Manager> findByWarehouseId(UUID warehouseId);
    List<Manager> findActive();
    Manager create(Manager manager);
    Optional<Manager> update(UUID id, Manager manager);
    void delete(UUID id);
    Optional<Manager> findByEmail(String email);
}