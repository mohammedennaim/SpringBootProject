package com.example.digitallogistics.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.model.dto.InventoryMovementCreateDto;
import com.example.digitallogistics.model.dto.InventoryMovementDto;
import com.example.digitallogistics.model.entity.InventoryMovement;
import com.example.digitallogistics.model.enums.MovementType;
import com.example.digitallogistics.service.InventoryMovementService;

@RestController
@RequestMapping("/api/inventory-movements")
public class InventoryMovementController {

    private final InventoryMovementService inventoryMovementService;

    public InventoryMovementController(InventoryMovementService inventoryMovementService) {
        this.inventoryMovementService = inventoryMovementService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryMovementDto>> list(@RequestParam(required = false) MovementType type) {
        List<InventoryMovement> moves = inventoryMovementService.findAll(Optional.ofNullable(type));
        List<InventoryMovementDto> dtos = moves.stream().map(m -> {
            InventoryMovementDto d = new InventoryMovementDto();
            d.setId(m.getId());
            d.setType(m.getType());
            d.setQuantity(m.getQuantity());
            d.setOccurredAt(m.getOccurredAt());
            d.setReference(m.getReference());
            d.setDescription(m.getDescription());
            return d;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/inbound")
    public ResponseEntity<InventoryMovementDto> inbound(@RequestBody @Valid InventoryMovementCreateDto dto) {
        InventoryMovement m = inventoryMovementService.recordInbound(dto);
        InventoryMovementDto d = new InventoryMovementDto();
        d.setId(m.getId()); d.setType(m.getType()); d.setQuantity(m.getQuantity()); d.setOccurredAt(m.getOccurredAt()); d.setReference(m.getReference()); d.setDescription(m.getDescription());
        return ResponseEntity.ok(d);
    }

    @PostMapping("/outbound")
    public ResponseEntity<InventoryMovementDto> outbound(@RequestBody @Valid InventoryMovementCreateDto dto) {
        InventoryMovement m = inventoryMovementService.recordOutbound(dto);
        InventoryMovementDto d = new InventoryMovementDto();
        d.setId(m.getId()); d.setType(m.getType()); d.setQuantity(m.getQuantity()); d.setOccurredAt(m.getOccurredAt()); d.setReference(m.getReference()); d.setDescription(m.getDescription());
        return ResponseEntity.ok(d);
    }

    @PostMapping("/adjustment")
    public ResponseEntity<InventoryMovementDto> adjustment(@RequestBody @Valid InventoryMovementCreateDto dto) {
        InventoryMovement m = inventoryMovementService.recordAdjustment(dto);
        InventoryMovementDto d = new InventoryMovementDto();
        d.setId(m.getId()); d.setType(m.getType()); d.setQuantity(m.getQuantity()); d.setOccurredAt(m.getOccurredAt()); d.setReference(m.getReference()); d.setDescription(m.getDescription());
        return ResponseEntity.ok(d);
    }
}
