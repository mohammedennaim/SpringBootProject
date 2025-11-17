package com.example.digitallogistics.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceImplTest {

    @Mock
    private ShipmentRepository shipmentRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private CarrierRepository carrierRepository;
    @Mock
    private ShipmentMapper shipmentMapper;

    @InjectMocks
    private ShipmentServiceImpl shipmentService;

    private UUID shipmentId;
    private UUID warehouseId;
    private UUID carrierId;
    private UUID orderId;
    private Shipment shipment;
    private ShipmentDto shipmentDto;
    private Warehouse warehouse;
    private Carrier carrier;

    @BeforeEach
    void setUp() {
        shipmentId = UUID.randomUUID();
        warehouseId = UUID.randomUUID();
        carrierId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        warehouse.setCode("WH-001");
        warehouse.setName("Main Warehouse");

        carrier = new Carrier();
        carrier.setId(carrierId);
        carrier.setName("DHL");
        carrier.setCode("DHL");

        shipment = Shipment.builder()
                .id(shipmentId)
                .orderId(orderId)
                .warehouse(warehouse)
                .carrier(carrier)
                .trackingNumber("TRACK123456")
                .status(ShipmentStatus.PLANNED)
                .build();

        shipmentDto = new ShipmentDto();
        shipmentDto.setId(shipmentId);
        shipmentDto.setOrderId(orderId);
        shipmentDto.setTrackingNumber("TRACK123456");
        shipmentDto.setStatus(ShipmentStatus.PLANNED);
    }

    @Test
    void getAllShipments_shouldReturnPagedShipments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shipment> page = new PageImpl<>(List.of(shipment), pageable, 1);

        when(shipmentRepository.findAll(pageable)).thenReturn(page);
        when(shipmentMapper.toDto(any(Shipment.class))).thenReturn(shipmentDto);

        Page<ShipmentDto> result = shipmentService.getAllShipments(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("TRACK123456", result.getContent().get(0).getTrackingNumber());
        verify(shipmentRepository).findAll(pageable);
    }

    @Test
    void getShipmentById_shouldReturnShipment() {
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        when(shipmentMapper.toDto(shipment)).thenReturn(shipmentDto);

        ShipmentDto result = shipmentService.getShipmentById(shipmentId);

        assertNotNull(result);
        assertEquals(shipmentId, result.getId());
        verify(shipmentRepository).findById(shipmentId);
    }

    @Test
    void getShipmentById_shouldThrowException_whenNotFound() {
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> shipmentService.getShipmentById(shipmentId));
    }

    @Test
    void createShipment_shouldCreateSuccessfully() {
        ShipmentCreateDto createDto = new ShipmentCreateDto();
        createDto.setOrderId(orderId);
        createDto.setWarehouseId(warehouseId);
        createDto.setCarrierId(carrierId);
        createDto.setTrackingNumber("TRACK123456");

        when(shipmentRepository.existsByOrderId(orderId)).thenReturn(false);
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);
        when(shipmentMapper.toDto(shipment)).thenReturn(shipmentDto);

        ShipmentDto result = shipmentService.createShipment(createDto);

        assertNotNull(result);
        assertEquals("TRACK123456", result.getTrackingNumber());
        verify(shipmentRepository).save(any(Shipment.class));
    }

    @Test
    void createShipment_shouldThrowException_whenOrderAlreadyHasShipment() {
        ShipmentCreateDto createDto = new ShipmentCreateDto();
        createDto.setOrderId(orderId);

        when(shipmentRepository.existsByOrderId(orderId)).thenReturn(true);

        assertThrows(ValidationException.class, 
            () -> shipmentService.createShipment(createDto));
    }

    @Test
    void createShipment_shouldThrowException_whenWarehouseNotFound() {
        ShipmentCreateDto createDto = new ShipmentCreateDto();
        createDto.setOrderId(orderId);
        createDto.setWarehouseId(warehouseId);

        when(shipmentRepository.existsByOrderId(orderId)).thenReturn(false);
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> shipmentService.createShipment(createDto));
    }

    @Test
    void updateShipmentStatus_shouldUpdateToInTransit() {
        ShipmentStatusUpdateDto statusUpdate = new ShipmentStatusUpdateDto();
        statusUpdate.setStatus(ShipmentStatus.IN_TRANSIT);

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(i -> i.getArgument(0));
        when(shipmentMapper.toDto(any(Shipment.class))).thenReturn(shipmentDto);

        ShipmentDto result = shipmentService.updateShipmentStatus(shipmentId, statusUpdate);

        assertNotNull(result);
        verify(shipmentRepository).save(any(Shipment.class));
    }

    @Test
    void getShipmentsByStatus_shouldReturnFilteredShipments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shipment> page = new PageImpl<>(List.of(shipment), pageable, 1);

        when(shipmentRepository.findByStatus(ShipmentStatus.PLANNED, pageable)).thenReturn(page);
        when(shipmentMapper.toDto(any(Shipment.class))).thenReturn(shipmentDto);

        Page<ShipmentDto> result = shipmentService.getShipmentsByStatus(ShipmentStatus.PLANNED, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(shipmentRepository).findByStatus(ShipmentStatus.PLANNED, pageable);
    }

    @Test
    void getShipmentByTrackingNumber_shouldReturnShipment() {
        when(shipmentRepository.findByTrackingNumber("TRACK123456")).thenReturn(Optional.of(shipment));
        when(shipmentMapper.toDto(shipment)).thenReturn(shipmentDto);

        ShipmentDto result = shipmentService.getShipmentByTrackingNumber("TRACK123456");

        assertNotNull(result);
        assertEquals("TRACK123456", result.getTrackingNumber());
        verify(shipmentRepository).findByTrackingNumber("TRACK123456");
    }

    @Test
    void getShipmentByTrackingNumber_shouldThrowException_whenNotFound() {
        when(shipmentRepository.findByTrackingNumber("INVALID")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> shipmentService.getShipmentByTrackingNumber("INVALID"));
    }



    @Test
    void getShipmentsByWarehouse_shouldReturnShipments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shipment> page = new PageImpl<>(List.of(shipment));
        when(shipmentRepository.findByWarehouseId(warehouseId, pageable)).thenReturn(page);
        when(shipmentMapper.toDto(any())).thenReturn(shipmentDto);
        Page<ShipmentDto> result = shipmentService.getShipmentsByWarehouse(warehouseId, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void createShipment_shouldThrowException_whenCarrierNotFound() {
        ShipmentCreateDto createDto = new ShipmentCreateDto();
        createDto.setOrderId(orderId);
        createDto.setWarehouseId(warehouseId);
        createDto.setCarrierId(carrierId);
        when(shipmentRepository.existsByOrderId(orderId)).thenReturn(false);
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> shipmentService.createShipment(createDto));
    }



    @Test
    void deleteShipment_shouldDeleteSuccessfully() {
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        shipmentService.deleteShipment(shipmentId);
        verify(shipmentRepository).delete(shipment);
    }

    @Test
    void deleteShipment_shouldThrowException_whenNotFound() {
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> shipmentService.deleteShipment(shipmentId));
    }

    @Test
    void updateShipmentStatus_shouldThrowException_forInvalidTransition() {
        shipment.setStatus(ShipmentStatus.DELIVERED);
        ShipmentStatusUpdateDto statusUpdate = new ShipmentStatusUpdateDto();
        statusUpdate.setStatus(ShipmentStatus.PLANNED);

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));

        assertThrows(ValidationException.class, 
            () -> shipmentService.updateShipmentStatus(shipmentId, statusUpdate));
    }

    @Test
    void updateShipmentStatus_shouldAllowSameStatus() {
        ShipmentStatusUpdateDto statusUpdate = new ShipmentStatusUpdateDto();
        statusUpdate.setStatus(ShipmentStatus.PLANNED);

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(i -> i.getArgument(0));
        when(shipmentMapper.toDto(any(Shipment.class))).thenReturn(shipmentDto);

        ShipmentDto result = shipmentService.updateShipmentStatus(shipmentId, statusUpdate);

        assertNotNull(result);
    }

    @Test
    void updateShipmentStatus_shouldUpdateToDelivered() {
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        ShipmentStatusUpdateDto statusUpdate = new ShipmentStatusUpdateDto();
        statusUpdate.setStatus(ShipmentStatus.DELIVERED);

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(i -> i.getArgument(0));
        when(shipmentMapper.toDto(any(Shipment.class))).thenReturn(shipmentDto);

        ShipmentDto result = shipmentService.updateShipmentStatus(shipmentId, statusUpdate);

        assertNotNull(result);
    }
}
