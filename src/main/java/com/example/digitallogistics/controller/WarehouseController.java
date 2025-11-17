package com.example.digitallogistics.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.model.dto.WarehouseCreateDto;
import com.example.digitallogistics.model.dto.WarehouseDto;
import com.example.digitallogistics.model.dto.WarehouseUpdateDto;
import com.example.digitallogistics.model.entity.Warehouse;
import com.example.digitallogistics.model.mapper.WarehouseMapper;
import com.example.digitallogistics.service.WarehouseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final WarehouseMapper warehouseMapper;

    public WarehouseController(WarehouseService warehouseService, WarehouseMapper warehouseMapper) {
        this.warehouseService = warehouseService;
        this.warehouseMapper = warehouseMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public List<WarehouseDto> list() {
        return warehouseService.findAll().stream()
                .map(warehouseMapper::toDto)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<WarehouseDto> create(@RequestBody @Valid WarehouseCreateDto createDto) {
        Warehouse warehouse = warehouseMapper.toEntity(createDto);
        if (warehouse.getActive() == null) {
            warehouse.setActive(true);
        }
        Warehouse saved = warehouseService.create(warehouse);
        WarehouseDto dto = warehouseMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/warehouses/" + dto.getId())).body(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<WarehouseDto> get(@PathVariable UUID id) {
        return warehouseService.findById(id)
                .map(warehouse -> ResponseEntity.ok(warehouseMapper.toDto(warehouse)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<WarehouseDto> update(@PathVariable UUID id, @RequestBody @Valid WarehouseUpdateDto updateDto) {
        Optional<Warehouse> existing = warehouseService.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        
        Warehouse warehouse = existing.get();
        warehouseMapper.updateFromDto(updateDto, warehouse);
        
        return warehouseService.update(id, warehouse)
                .map(updated -> ResponseEntity.ok(warehouseMapper.toDto(updated)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return warehouseService.findById(id)
                .map(warehouse -> {
                    warehouseService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}