package com.example.digitallogistics.service;

import java.util.List;

import com.example.digitallogistics.model.dto.InventoryMovementCreateDto;
import com.example.digitallogistics.model.entity.InventoryMovement;

public interface InventoryMovementService {
    List<InventoryMovement> findAll();
    InventoryMovement recordInbound(InventoryMovementCreateDto dto);
    InventoryMovement recordOutbound(InventoryMovementCreateDto dto);
    InventoryMovement recordAdjustment(InventoryMovementCreateDto dto);
}
