package com.example.digitallogistics.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.digitallogistics.model.entity.Warehouse;
import com.example.digitallogistics.repository.WarehouseRepository;
import com.example.digitallogistics.service.WarehouseService;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    @Override
    public List<Warehouse> findAllActive() {
        return warehouseRepository.findByActiveTrue();
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Warehouse> findById(UUID id) {
        return warehouseRepository.findById(id);
    }

    @Override
    public List<Warehouse> findByCode(String code) {
        return warehouseRepository.findByCode(code);
    }

    @SuppressWarnings("null")
    @Override
    public Warehouse create(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Warehouse> update(UUID id, Warehouse warehouse) {
        if (warehouseRepository.existsById(id)) {
            warehouse.setId(id);
            return Optional.of(warehouseRepository.save(warehouse));
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(UUID id) {
        @SuppressWarnings("null")
        Optional<Warehouse> warehouse = warehouseRepository.findById(id);
        if (warehouse.isPresent()) {
            Warehouse w = warehouse.get();
            w.setActive(false);
            warehouseRepository.save(w);
        }
    }

}