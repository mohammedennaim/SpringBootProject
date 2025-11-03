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

    // GET /api/warehouses - Liste des entrepôts
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<WarehouseDto> list() {
        return warehouseService.findAll().stream()
                .map(warehouseMapper::toDto)
                .collect(Collectors.toList());
    }

    // POST /api/warehouses - Création d'un entrepôt
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<WarehouseDto> create(@RequestBody @Valid WarehouseCreateDto createDto) {
        Warehouse warehouse = warehouseMapper.toEntity(createDto);
        // Set default active status if not provided
        if (warehouse.getActive() == null) {
            warehouse.setActive(true);
        }
        Warehouse saved = warehouseService.create(warehouse);
        WarehouseDto dto = warehouseMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/warehouses/" + dto.getId())).body(dto);
    }

    // GET /api/warehouses/{id} - Détails d'un entrepôt
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<WarehouseDto> get(@PathVariable UUID id) {
        Optional<Warehouse> warehouse = warehouseService.findById(id);
        if (warehouse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(warehouseMapper.toDto(warehouse.get()));
    }

    // PUT /api/warehouses/{id} - Mise à jour
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<WarehouseDto> update(@PathVariable UUID id, @RequestBody @Valid WarehouseUpdateDto updateDto) {
        Optional<Warehouse> existing = warehouseService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Warehouse warehouse = existing.get();
        warehouseMapper.updateFromDto(updateDto, warehouse);
        
        Optional<Warehouse> updated = warehouseService.update(id, warehouse);
        if (updated.isPresent()) {
            return ResponseEntity.ok(warehouseMapper.toDto(updated.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /api/warehouses/{id} - Suppression / désactivation
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        Optional<Warehouse> warehouse = warehouseService.findById(id);
        if (warehouse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        warehouseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}