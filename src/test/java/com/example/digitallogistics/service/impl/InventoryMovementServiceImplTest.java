package com.example.digitallogistics.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import com.example.digitallogistics.model.dto.InventoryMovementCreateDto;
import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.entity.InventoryMovement;
import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.repository.InventoryMovementRepository;
import com.example.digitallogistics.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryMovementServiceImplTest {

    @Mock
    InventoryMovementRepository inventoryMovementRepository;
    @Mock
    InventoryRepository inventoryRepository;

    @InjectMocks
    InventoryMovementServiceImpl service;

    @Test
    void recordInbound_shouldUpdateInventoryAndSaveMovement() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = new Product();
        product.setId(productId);
        
        Inventory inv = Inventory.builder()
                .id(UUID.randomUUID())
                .product(product)
                .qtyOnHand(2)
                .qtyReserved(0)
                .build();
        when(inventoryRepository.findByWarehouseId(warehouseId)).thenReturn(List.of(inv));
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(inventoryMovementRepository.save(any())).thenAnswer(i -> {
            InventoryMovement m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        InventoryMovementCreateDto dto = new InventoryMovementCreateDto();
        dto.setProductId(productId);
        dto.setQuantity(5);
        dto.setWarehouseId(warehouseId);
        dto.setReference("ref");
        dto.setDescription("inbound");

        InventoryMovement res = service.recordInbound(dto);
        assertNotNull(res.getId());
        verify(inventoryRepository, atLeastOnce()).save(any(Inventory.class));
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    void recordOutbound_shouldDecreaseInventory() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = new Product();
        product.setId(productId);

        Inventory inv = Inventory.builder()
                .id(UUID.randomUUID())
                .product(product)
                .qtyOnHand(10)
                .qtyReserved(0)
                .build();
        
        when(inventoryRepository.findByWarehouseId(warehouseId)).thenReturn(List.of(inv));
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(inventoryMovementRepository.save(any())).thenAnswer(i -> {
            InventoryMovement m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        InventoryMovementCreateDto dto = new InventoryMovementCreateDto();
        dto.setProductId(productId);
        dto.setQuantity(3);
        dto.setWarehouseId(warehouseId);
        dto.setReference("sale");
        dto.setDescription("outbound sale");

        InventoryMovement res = service.recordOutbound(dto);
        
        assertNotNull(res);
        assertNotNull(res.getId());
        verify(inventoryRepository).save(any(Inventory.class));
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    void recordAdjustment_shouldSaveMovement() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = new Product();
        product.setId(productId);

        Inventory inv = Inventory.builder()
                .id(UUID.randomUUID())
                .product(product)
                .qtyOnHand(5)
                .qtyReserved(0)
                .build();
        
        when(inventoryRepository.findByWarehouseId(warehouseId)).thenReturn(List.of(inv));
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(inventoryMovementRepository.save(any())).thenAnswer(i -> {
            InventoryMovement m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        InventoryMovementCreateDto dto = new InventoryMovementCreateDto();
        dto.setProductId(productId);
        dto.setQuantity(2);
        dto.setWarehouseId(warehouseId);
        dto.setReference("adjustment");
        dto.setDescription("manual adjustment");

        InventoryMovement res = service.recordAdjustment(dto);
        
        assertNotNull(res);
        assertNotNull(res.getId());
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    void findAll_shouldReturnAllMovements() {
        InventoryMovement movement = InventoryMovement.builder()
                .id(UUID.randomUUID())
                .quantity(5)
                .build();
        
        when(inventoryMovementRepository.findAll()).thenReturn(List.of(movement));

        List<InventoryMovement> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(inventoryMovementRepository).findAll();
    }
}
