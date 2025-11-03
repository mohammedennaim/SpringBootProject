package com.example.digitallogistics.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.example.digitallogistics.model.entity.Supplier;

public interface SupplierService {
    List<Supplier> findAll();
    Optional<Supplier> findById(UUID id);
    List<Supplier> findByNameContaining(String namePart);
    Supplier create(Supplier supplier);
    Optional<Supplier> update(UUID id, Supplier supplier);
    void deleteById(UUID id);
}