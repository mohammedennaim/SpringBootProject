package com.example.digitallogistics.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.digitallogistics.model.dto.ShipmentCreateDto;
import com.example.digitallogistics.model.dto.ShipmentDto;
import com.example.digitallogistics.model.dto.ShipmentStatusUpdateDto;
import com.example.digitallogistics.model.enums.ShipmentStatus;

public interface ShipmentService {

    /**
     * Récupère toutes les expéditions avec pagination
     */
    Page<ShipmentDto> getAllShipments(Pageable pageable);

    /**
     * Récupère une expédition par son ID
     */
    ShipmentDto getShipmentById(UUID id);

    /**
     * Crée une nouvelle expédition pour une commande RESERVED
     */
    ShipmentDto createShipment(ShipmentCreateDto createDto);

    /**
     * Met à jour le statut d'une expédition (PLANNED → IN_TRANSIT → DELIVERED)
     */
    ShipmentDto updateShipmentStatus(UUID id, ShipmentStatusUpdateDto statusUpdate);

    /**
     * Recherche des expéditions par statut
     */
    Page<ShipmentDto> getShipmentsByStatus(ShipmentStatus status, Pageable pageable);

    /**
     * Recherche des expéditions par entrepôt
     */
    Page<ShipmentDto> getShipmentsByWarehouse(UUID warehouseId, Pageable pageable);

    /**
     * Recherche d'une expédition par numéro de suivi
     */
    ShipmentDto getShipmentByTrackingNumber(String trackingNumber);

    /**
     * Recherche combinée par statut et entrepôt
     */
    Page<ShipmentDto> getShipmentsByStatusAndWarehouse(
        ShipmentStatus status, UUID warehouseId, Pageable pageable);

    /**
     * Supprime une expédition (soft delete ou hard delete selon la logique métier)
     */
    void deleteShipment(UUID id);
}