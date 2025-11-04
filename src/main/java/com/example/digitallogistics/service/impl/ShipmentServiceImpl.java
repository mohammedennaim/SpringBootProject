package com.example.digitallogistics.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.digitallogistics.exception.ResourceNotFoundException;
import com.example.digitallogistics.exception.ValidationException;
import com.example.digitallogistics.model.dto.ShipmentCreateDto;
import com.example.digitallogistics.model.dto.ShipmentDto;
import com.example.digitallogistics.model.dto.ShipmentStatusUpdateDto;
import com.example.digitallogistics.model.entity.Carrier;
import com.example.digitallogistics.model.entity.Shipment;
import com.example.digitallogistics.model.entity.Warehouse;
import com.example.digitallogistics.model.enums.ShipmentStatus;
import com.example.digitallogistics.model.mapper.ShipmentMapper;
import com.example.digitallogistics.repository.CarrierRepository;
import com.example.digitallogistics.repository.ShipmentRepository;
import com.example.digitallogistics.repository.WarehouseRepository;
import com.example.digitallogistics.service.ShipmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final WarehouseRepository warehouseRepository;
    private final CarrierRepository carrierRepository;
    private final ShipmentMapper shipmentMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentDto> getAllShipments(Pageable pageable) {
        log.debug("Retrieving all shipments with pagination: {}", pageable);
        Page<Shipment> shipments = shipmentRepository.findAll(pageable);
        return shipments.map(shipmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentDto getShipmentById(UUID id) {
        log.debug("Retrieving shipment by id: {}", id);
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        return shipmentMapper.toDto(shipment);
    }

    @Override
    public ShipmentDto createShipment(ShipmentCreateDto createDto) {
        log.debug("Creating new shipment for order: {}", createDto.getOrderId());
        
        // Vérifier que la commande n'a pas déjà une expédition
        if (shipmentRepository.existsByOrderId(createDto.getOrderId())) {
            throw new ValidationException("Order already has a shipment: " + createDto.getOrderId());
        }

        // Vérifier que l'entrepôt existe
        Warehouse warehouse = warehouseRepository.findById(createDto.getWarehouseId())
            .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + createDto.getWarehouseId()));

        // Vérifier le transporteur s'il est spécifié
        Carrier carrier = null;
        if (createDto.getCarrierId() != null) {
            carrier = carrierRepository.findById(createDto.getCarrierId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + createDto.getCarrierId()));
        }

        // Générer un numéro de suivi unique
        String trackingNumber = createDto.getTrackingNumber();
        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            trackingNumber = generateTrackingNumber();
        }

        // Créer l'expédition
        Shipment shipment = Shipment.builder()
            .orderId(createDto.getOrderId())
            .warehouse(warehouse)
            .carrier(carrier)
            .trackingNumber(trackingNumber)
            .status(ShipmentStatus.PLANNED)
            .shippedAt(LocalDateTime.now())
            .build();

        Shipment savedShipment = shipmentRepository.save(shipment);
        log.info("Created new shipment with id: {} for order: {}", savedShipment.getId(), createDto.getOrderId());
        
        return shipmentMapper.toDto(savedShipment);
    }

    @Override
    public ShipmentDto updateShipmentStatus(UUID id, ShipmentStatusUpdateDto statusUpdate) {
        log.debug("Updating shipment status to: {} for shipment: {}", statusUpdate.getStatus(), id);
        
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        // Valider la transition de statut
        validateStatusTransition(shipment.getStatus(), statusUpdate.getStatus());

        // Mettre à jour le statut et les dates appropriées
        shipment.setStatus(statusUpdate.getStatus());
        
        switch (statusUpdate.getStatus()) {
            case IN_TRANSIT:
                if (shipment.getShippedAt() == null) {
                    shipment.setShippedAt(LocalDateTime.now());
                }
                break;
            case DELIVERED:
                if (shipment.getDeliveredAt() == null) {
                    shipment.setDeliveredAt(LocalDateTime.now());
                }
                break;
            default:
                // Aucune action spéciale pour PLANNED
                break;
        }

        Shipment updatedShipment = shipmentRepository.save(shipment);
        log.info("Updated shipment {} status to: {}", id, statusUpdate.getStatus());
        
        return shipmentMapper.toDto(updatedShipment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentDto> getShipmentsByStatus(ShipmentStatus status, Pageable pageable) {
        log.debug("Retrieving shipments by status: {}", status);
        Page<Shipment> shipments = shipmentRepository.findByStatus(status, pageable);
        return shipments.map(shipmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentDto> getShipmentsByWarehouse(UUID warehouseId, Pageable pageable) {
        log.debug("Retrieving shipments by warehouse: {}", warehouseId);
        Page<Shipment> shipments = shipmentRepository.findByWarehouseId(warehouseId, pageable);
        return shipments.map(shipmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentDto getShipmentByTrackingNumber(String trackingNumber) {
        log.debug("Retrieving shipment by tracking number: {}", trackingNumber);
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with tracking number: " + trackingNumber));
        return shipmentMapper.toDto(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentDto> getShipmentsByStatusAndWarehouse(
            ShipmentStatus status, UUID warehouseId, Pageable pageable) {
        log.debug("Retrieving shipments by status: {} and warehouse: {}", status, warehouseId);
        Page<Shipment> shipments = shipmentRepository.findByStatusAndWarehouseId(status, warehouseId, pageable);
        return shipments.map(shipmentMapper::toDto);
    }

    @Override
    public void deleteShipment(UUID id) {
        log.debug("Deleting shipment: {}", id);
        
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        // Vérifier que l'expédition peut être supprimée
        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new ValidationException("Cannot delete a delivered shipment");
        }

        shipmentRepository.delete(shipment);
        log.info("Deleted shipment: {}", id);
    }

    /**
     * Valide les transitions de statut autorisées
     */
    private void validateStatusTransition(ShipmentStatus currentStatus, ShipmentStatus newStatus) {
        if (currentStatus == newStatus) {
            return; // Pas de changement
        }

        switch (currentStatus) {
            case PLANNED:
                if (newStatus != ShipmentStatus.IN_TRANSIT) {
                    throw new ValidationException("Can only transition from PLANNED to IN_TRANSIT");
                }
                break;
            case IN_TRANSIT:
                if (newStatus != ShipmentStatus.DELIVERED) {
                    throw new ValidationException("Can only transition from IN_TRANSIT to DELIVERED");
                }
                break;
            case DELIVERED:
                throw new ValidationException("Cannot change status of a delivered shipment");
            default:
                throw new ValidationException("Invalid status transition");
        }
    }

    /**
     * Génère un numéro de suivi unique
     */
    private String generateTrackingNumber() {
        return "TRK-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}