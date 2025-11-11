package com.example.digitallogistics.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.digitallogistics.model.dto.ShipmentCreateDto;
import com.example.digitallogistics.model.dto.ShipmentDto;
import com.example.digitallogistics.model.dto.ShipmentStatusUpdateDto;
import com.example.digitallogistics.model.enums.ShipmentStatus;

public interface ShipmentService {
    Page<ShipmentDto> getAllShipments(Pageable pageable);
    ShipmentDto getShipmentById(UUID id);
    ShipmentDto createShipment(ShipmentCreateDto createDto);
    ShipmentDto updateShipmentStatus(UUID id, ShipmentStatusUpdateDto statusUpdate);
    Page<ShipmentDto> getShipmentsByStatus(ShipmentStatus status, Pageable pageable);
    Page<ShipmentDto> getShipmentsByWarehouse(UUID warehouseId, Pageable pageable);
    ShipmentDto getShipmentByTrackingNumber(String trackingNumber);
    Page<ShipmentDto> getShipmentsByStatusAndWarehouse(
    ShipmentStatus status, UUID warehouseId, Pageable pageable);
    void deleteShipment(UUID id);
}