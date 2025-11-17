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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.model.dto.SupplierCreateDto;
import com.example.digitallogistics.model.dto.SupplierDto;
import com.example.digitallogistics.model.dto.SupplierUpdateDto;
import com.example.digitallogistics.model.entity.Supplier;
import com.example.digitallogistics.model.mapper.SupplierMapper;
import com.example.digitallogistics.service.SupplierService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;
    private final SupplierMapper supplierMapper;

    public SupplierController(SupplierService supplierService, SupplierMapper supplierMapper) {
        this.supplierService = supplierService;
        this.supplierMapper = supplierMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public List<SupplierDto> list(@RequestParam(required = false) String search) {
        List<Supplier> suppliers;
        
        if (search != null && !search.trim().isEmpty()) {
            suppliers = supplierService.findByNameContaining(search.trim());
        } else {
            suppliers = supplierService.findAll();
        }
        
        return suppliers.stream()
                .map(supplierMapper::toDto)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<SupplierDto> create(@RequestBody @Valid SupplierCreateDto createDto) {
        Supplier supplier = supplierMapper.toEntity(createDto);
        Supplier saved = supplierService.create(supplier);
        SupplierDto dto = supplierMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/suppliers/" + dto.getId())).body(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<SupplierDto> get(@PathVariable UUID id) {
        return supplierService.findById(id)
                .map(supplier -> ResponseEntity.ok(supplierMapper.toDto(supplier)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<SupplierDto> update(@PathVariable UUID id, @RequestBody @Valid SupplierUpdateDto updateDto) {
        Optional<Supplier> existing = supplierService.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        
        Supplier supplier = existing.get();
        supplierMapper.updateFromDto(updateDto, supplier);
        
        return supplierService.update(id, supplier)
                .map(updated -> ResponseEntity.ok(supplierMapper.toDto(updated)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return supplierService.findById(id)
                .map(supplier -> {
                    supplierService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}