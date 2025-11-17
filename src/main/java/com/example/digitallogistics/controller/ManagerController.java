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
        return managerService.findById(id)
                .map(manager -> ResponseEntity.ok(managerMapper.toDto(manager)))
                .orElseGet(() -> ResponseEntity.notFound().build());
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
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Manager manager = managerMapper.toEntity(managerCreateDto);
        Manager createdManager = managerService.create(manager);
        ManagerDto managerDto = managerMapper.toDto(createdManager);
        return ResponseEntity.status(HttpStatus.CREATED).body(managerDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ManagerDto> updateManager(@PathVariable UUID id, @Valid @RequestBody ManagerUpdateDto managerUpdateDto) {
        if (managerUpdateDto.getEmail() != null) {
            Optional<Manager> existingManager = managerService.findByEmail(managerUpdateDto.getEmail());
            if (existingManager.isPresent() && !existingManager.get().getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
        
        Manager manager = new Manager();
        managerMapper.updateEntityFromDto(managerUpdateDto, manager);
        
        return managerService.update(id, manager)
                .map(updated -> ResponseEntity.ok(managerMapper.toDto(updated)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteManager(@PathVariable UUID id) {
        if (managerService.findById(id).isPresent()) {
            managerService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
            return managerService.findById(managerId)
                    .map(manager -> ResponseEntity.status(HttpStatus.OK).body(managerMapper.toDto(manager)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{managerId}/warehouses/{warehouseId}")
    @Operation(summary = "Retirer un entrepôt d'un manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeWarehouse(
            @PathVariable UUID managerId, 
            @PathVariable UUID warehouseId) {
        try {
            ((ManagerServiceImpl) managerService)
                .removeWarehouse(managerId, warehouseId);
            return managerService.findById(managerId).isPresent() 
                    ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
            return managerService.findById(managerId)
                    .map(manager -> ResponseEntity.status(HttpStatus.OK).body(managerMapper.toDto(manager)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}