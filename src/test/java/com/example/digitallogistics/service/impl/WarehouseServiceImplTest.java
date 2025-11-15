package com.example.digitallogistics.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.digitallogistics.model.entity.Warehouse;
import com.example.digitallogistics.repository.WarehouseRepository;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceImplTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private UUID warehouseId;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouseId = UUID.randomUUID();
        warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        warehouse.setCode("WH-001");
        warehouse.setName("Main Warehouse");
        warehouse.setActive(true);
    }

    @Test
    void findAll_shouldReturnAllWarehouses() {
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<Warehouse> result = warehouseService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WH-001", result.get(0).getCode());
        verify(warehouseRepository).findAll();
    }

    @Test
    void findAllActive_shouldReturnOnlyActiveWarehouses() {
        when(warehouseRepository.findByActiveTrue()).thenReturn(List.of(warehouse));

        List<Warehouse> result = warehouseService.findAllActive();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());
        verify(warehouseRepository).findByActiveTrue();
    }

    @Test
    void findById_shouldReturnWarehouse() {
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));

        Optional<Warehouse> result = warehouseService.findById(warehouseId);

        assertTrue(result.isPresent());
        assertEquals(warehouseId, result.get().getId());
        verify(warehouseRepository).findById(warehouseId);
    }

    @Test
    void findByCode_shouldReturnWarehouses() {
        when(warehouseRepository.findByCode("WH-001")).thenReturn(List.of(warehouse));

        List<Warehouse> result = warehouseService.findByCode("WH-001");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WH-001", result.get(0).getCode());
    }

    @Test
    void create_shouldSaveWarehouse() {
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        Warehouse result = warehouseService.create(warehouse);

        assertNotNull(result);
        assertEquals("WH-001", result.getCode());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void update_shouldUpdateExistingWarehouse() {
        Warehouse updates = new Warehouse();
        updates.setCode("WH-002");
        updates.setName("Updated Warehouse");
        updates.setActive(false);

        when(warehouseRepository.existsById(warehouseId)).thenReturn(true);
        when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Warehouse> result = warehouseService.update(warehouseId, updates);

        assertTrue(result.isPresent());
        assertEquals(warehouseId, result.get().getId());
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void update_shouldReturnEmpty_whenWarehouseNotFound() {
        when(warehouseRepository.existsById(warehouseId)).thenReturn(false);

        Optional<Warehouse> result = warehouseService.update(warehouseId, new Warehouse());

        assertFalse(result.isPresent());
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void deleteById_shouldDeactivateWarehouse() {
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(i -> i.getArgument(0));

        warehouseService.deleteById(warehouseId);

        verify(warehouseRepository).findById(warehouseId);
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void deleteById_shouldDoNothing_whenWarehouseNotFound() {
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        warehouseService.deleteById(warehouseId);

        verify(warehouseRepository).findById(warehouseId);
        verify(warehouseRepository, never()).save(any());
    }
}
