package com.example.digitallogistics.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.example.digitallogistics.model.entity.Warehouse;

public interface WarehouseService {
    List<Warehouse> findAll();
    List<Warehouse> findAllActive();
    Optional<Warehouse> findById(UUID id);
    List<Warehouse> findByCode(String code);
    Warehouse create(Warehouse warehouse);
    Optional<Warehouse> update(UUID id, Warehouse warehouse);
    void deleteById(UUID id);
}