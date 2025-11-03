package com.example.digitallogistics.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.digitallogistics.model.entity.Supplier;
import com.example.digitallogistics.repository.SupplierRepository;
import com.example.digitallogistics.service.SupplierService;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    @Override
    public Optional<Supplier> findById(UUID id) {
        return supplierRepository.findById(id);
    }

    @Override
    public List<Supplier> findByNameContaining(String namePart) {
        return supplierRepository.findByNameContainingIgnoreCase(namePart);
    }

    @Override
    public Supplier create(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    public Optional<Supplier> update(UUID id, Supplier supplier) {
        if (supplierRepository.existsById(id)) {
            supplier.setId(id);
            return Optional.of(supplierRepository.save(supplier));
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(UUID id) {
        supplierRepository.deleteById(id);
    }
}