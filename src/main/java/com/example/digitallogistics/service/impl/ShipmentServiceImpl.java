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

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final WarehouseRepository warehouseRepository;
    private final CarrierRepository carrierRepository;
    private final ShipmentMapper shipmentMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentDto> getAllShipments(Pageable pageable) {
        @SuppressWarnings("null")
        Page<Shipment> shipments = shipmentRepository.findAll(pageable);
        return shipments.map(shipmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentDto getShipmentById(UUID id) {
        @SuppressWarnings("null")
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        return shipmentMapper.toDto(shipment);
    }

    @SuppressWarnings("null")
    @Override
    public ShipmentDto createShipment(ShipmentCreateDto createDto) {
        if (shipmentRepository.existsByOrderId(createDto.getOrderId())) {
            throw new ValidationException("Order already has a shipment: " + createDto.getOrderId());
        }
        
        Warehouse warehouse = warehouseRepository.findById(createDto.getWarehouseId())
            .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + createDto.getWarehouseId()));

        Carrier carrier = null;
        if (createDto.getCarrierId() != null) {
            carrier = carrierRepository.findById(createDto.getCarrierId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + createDto.getCarrierId()));
        }

        String trackingNumber = createDto.getTrackingNumber();
        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            trackingNumber = generateTrackingNumber();
        }

        Shipment shipment = Shipment.builder()
            .orderId(createDto.getOrderId())
            .warehouse(warehouse)
            .carrier(carrier)
            .trackingNumber(trackingNumber)
            .status(ShipmentStatus.PLANNED)
            .shippedAt(LocalDateTime.now())
            .build();

        Shipment savedShipment = shipmentRepository.save(shipment);
        return shipmentMapper.toDto(savedShipment);
    }

    @Override
    public ShipmentDto updateShipmentStatus(UUID id, ShipmentStatusUpdateDto statusUpdate) {
        @SuppressWarnings("null")
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        validateStatusTransition(shipment.getStatus(), statusUpdate.getStatus());
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
                break;
        }

        Shipment updatedShipment = shipmentRepository.save(shipment);
        return shipmentMapper.toDto(updatedShipment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentDto> getShipmentsByStatus(ShipmentStatus status, Pageable pageable) {
        Page<Shipment> shipments = shipmentRepository.findByStatus(status, pageable);
        return shipments.map(shipmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentDto> getShipmentsByWarehouse(UUID warehouseId, Pageable pageable) {
        Page<Shipment> shipments = shipmentRepository.findByWarehouseId(warehouseId, pageable);
        return shipments.map(shipmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentDto getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with tracking number: " + trackingNumber));
        return shipmentMapper.toDto(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentDto> getShipmentsByStatusAndWarehouse(
            ShipmentStatus status, UUID warehouseId, Pageable pageable) {
        Page<Shipment> shipments = shipmentRepository.findByStatusAndWarehouseId(status, warehouseId, pageable);
        return shipments.map(shipmentMapper::toDto);
    }

    @Override
    public void deleteShipment(UUID id) {
        @SuppressWarnings("null")
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new ValidationException("Cannot delete a delivered shipment");
        }

        shipmentRepository.delete(shipment);
    }

    private void validateStatusTransition(ShipmentStatus currentStatus, ShipmentStatus newStatus) {
        if (currentStatus == newStatus) {
            return;
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

    
    private String generateTrackingNumber() {
        return "TRK-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}