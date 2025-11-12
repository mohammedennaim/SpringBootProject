package com.example.digitallogistics.service;

import java.util.List;
import java.util.Optional;

import com.example.digitallogistics.model.dto.InventoryMovementCreateDto;
import com.example.digitallogistics.model.entity.InventoryMovement;
import com.example.digitallogistics.model.enums.MovementType;

public interface InventoryMovementService {
    List<InventoryMovement> findAll(Optional<MovementType> type);
    InventoryMovement recordInbound(InventoryMovementCreateDto dto);
    InventoryMovement recordOutbound(InventoryMovementCreateDto dto);
    InventoryMovement recordAdjustment(InventoryMovementCreateDto dto);
}
