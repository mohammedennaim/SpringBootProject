package com.example.digitallogistics.controller;

import com.example.digitallogistics.model.dto.ManagerCreateDto;
import com.example.digitallogistics.model.dto.ManagerDto;
import com.example.digitallogistics.model.dto.ManagerUpdateDto;
import com.example.digitallogistics.model.entity.Manager;
import com.example.digitallogistics.model.mapper.ManagerMapper;
import com.example.digitallogistics.service.ManagerService;
import com.example.digitallogistics.service.impl.ManagerServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/managers")
@Tag(name = "Managers", description = "API de gestion des managers (accessible uniquement par les admins)")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private ManagerMapper managerMapper;

    @GetMapping
    @Operation(summary = "Obtenir la liste de tous les managers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ManagerDto>> getAllManagers() {
        List<Manager> managers = managerService.findAll();
        List<ManagerDto> managerDtos = managers.stream()
                .map(managerMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(managerDtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ManagerDto> getManagerById(@PathVariable UUID id) {
        Optional<Manager> manager = managerService.findById(id);
        if (manager.isPresent()) {
            ManagerDto managerDto = managerMapper.toDto(manager.get());
            return ResponseEntity.ok(managerDto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Obtenir les managers d'un entrepôt")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ManagerDto>> getManagersByWarehouse(@PathVariable UUID warehouseId) {
        List<Manager> managers = managerService.findByWarehouseId(warehouseId);
        List<ManagerDto> managerDtos = managers.stream()
                .map(managerMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(managerDtos);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtenir la liste des managers actifs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ManagerDto>> getActiveManagers() {
        List<Manager> managers = managerService.findActive();
        List<ManagerDto> managerDtos = managers.stream()
                .map(managerMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(managerDtos);
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ManagerDto> createManager(@Valid @RequestBody ManagerCreateDto managerCreateDto) {
        if (managerService.findByEmail(managerCreateDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        Manager manager = managerMapper.toEntity(managerCreateDto);
        Manager createdManager = managerService.create(manager);
        ManagerDto managerDto = managerMapper.toDto(createdManager);
        return new ResponseEntity<>(managerDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ManagerDto> updateManager(@PathVariable UUID id, @Valid @RequestBody ManagerUpdateDto managerUpdateDto) {
        if (managerUpdateDto.getEmail() != null) {
            Optional<Manager> existingManager = managerService.findByEmail(managerUpdateDto.getEmail());
            if (existingManager.isPresent() && !existingManager.get().getId().equals(id)) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        Manager manager = new Manager();
        managerMapper.updateEntityFromDto(managerUpdateDto, manager);
        
        Optional<Manager> updatedManager = managerService.update(id, manager);
        if (updatedManager.isPresent()) {
            ManagerDto managerDto = managerMapper.toDto(updatedManager.get());
            return ResponseEntity.ok(managerDto);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteManager(@PathVariable UUID id) {
        if (managerService.findById(id).isPresent()) {
            managerService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{managerId}/warehouses/{warehouseId}")
    @Operation(summary = "Assigner un entrepôt à un manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ManagerDto> assignWarehouse(
            @PathVariable UUID managerId, 
            @PathVariable UUID warehouseId) {
        try {
            ((ManagerServiceImpl) managerService)
                .assignWarehouse(managerId, warehouseId);
            Optional<Manager> updatedManager = managerService.findById(managerId);
            if (updatedManager.isPresent()) {
                return ResponseEntity.ok(managerMapper.toDto(updatedManager.get()));
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{managerId}/warehouses/{warehouseId}")
    @Operation(summary = "Retirer un entrepôt d'un manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ManagerDto> removeWarehouse(
            @PathVariable UUID managerId, 
            @PathVariable UUID warehouseId) {
        try {
            ((ManagerServiceImpl) managerService)
                .removeWarehouse(managerId, warehouseId);
            Optional<Manager> updatedManager = managerService.findById(managerId);
            if (updatedManager.isPresent()) {
                return ResponseEntity.ok(managerMapper.toDto(updatedManager.get()));
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{managerId}/warehouses/batch")
    @Operation(summary = "Assigner plusieurs entrepôts à un manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ManagerDto> assignWarehouses(
            @PathVariable UUID managerId, 
            @RequestBody List<UUID> warehouseIds) {
        try {
            ((ManagerServiceImpl) managerService)
                .assignWarehouses(managerId, warehouseIds);
            Optional<Manager> updatedManager = managerService.findById(managerId);
            if (updatedManager.isPresent()) {
                return ResponseEntity.ok(managerMapper.toDto(updatedManager.get()));
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}