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

    // GET /api/suppliers - Liste des fournisseurs
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

    // POST /api/suppliers - Création d'un fournisseur
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
        Optional<Supplier> supplier = supplierService.findById(id);
        if (supplier.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(supplierMapper.toDto(supplier.get()));
    }

    // PUT /api/suppliers/{id} - Mise à jour
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<SupplierDto> update(@PathVariable UUID id, @RequestBody @Valid SupplierUpdateDto updateDto) {
        Optional<Supplier> existing = supplierService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Supplier supplier = existing.get();
        supplierMapper.updateFromDto(updateDto, supplier);
        
        Optional<Supplier> updated = supplierService.update(id, supplier);
        if (updated.isPresent()) {
            return ResponseEntity.ok(supplierMapper.toDto(updated.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /api/suppliers/{id} - Suppression
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        Optional<Supplier> supplier = supplierService.findById(id);
        if (supplier.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        supplierService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}