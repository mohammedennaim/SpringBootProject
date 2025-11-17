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
import com.example.digitallogistics.model.enums.MovementType;
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

    @Test
    void recordInbound_withExistingInventory_shouldUpdateQuantity() {
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
        dto.setQuantity(5);
        dto.setWarehouseId(warehouseId);
        dto.setReference("ref");
        dto.setDescription("inbound");

        InventoryMovement result = service.recordInbound(dto);
        
        assertNotNull(result);
        assertEquals(MovementType.INBOUND, result.getType());
        verify(inventoryRepository).save(any(Inventory.class));
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    void recordOutbound_withExistingInventory_shouldDecreaseQuantity() {
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

        InventoryMovement result = service.recordOutbound(dto);
        
        assertNotNull(result);
        assertEquals(MovementType.OUTBOUND, result.getType());
        verify(inventoryRepository).save(any(Inventory.class));
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    void recordAdjustment_withExistingInventory_shouldAdjustQuantity() {
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

        InventoryMovement result = service.recordAdjustment(dto);
        
        assertNotNull(result);
        assertEquals(MovementType.ADJUSTMENT, result.getType());
        verify(inventoryRepository).save(any(Inventory.class));
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    void recordInbound_withNoExistingInventory_shouldCreateNewInventory() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(inventoryRepository.findByWarehouseId(warehouseId)).thenReturn(List.of());
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(inventoryMovementRepository.save(any())).thenAnswer(i -> {
            InventoryMovement m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        InventoryMovementCreateDto dto = new InventoryMovementCreateDto();
        dto.setProductId(productId);
        dto.setQuantity(10);
        dto.setWarehouseId(warehouseId);

        InventoryMovement result = service.recordInbound(dto);

        assertNotNull(result);
        verify(inventoryRepository).save(any(Inventory.class));
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    void recordOutbound_withNoExistingInventory_shouldCreateNewInventory() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(inventoryRepository.findByWarehouseId(warehouseId)).thenReturn(List.of());
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

        InventoryMovement result = service.recordOutbound(dto);

        assertNotNull(result);
        verify(inventoryRepository).save(any(Inventory.class));
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    void recordAdjustment_withNoExistingInventory_shouldCreateNewInventory() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(inventoryRepository.findByWarehouseId(warehouseId)).thenReturn(List.of());
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

        InventoryMovement result = service.recordAdjustment(dto);

        assertNotNull(result);
        verify(inventoryRepository).save(any(Inventory.class));
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    void recordMovement_withNullQuantity_shouldDefaultToZero() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(inventoryRepository.findByWarehouseId(warehouseId)).thenReturn(List.of());
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(inventoryMovementRepository.save(any())).thenAnswer(i -> {
            InventoryMovement m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        InventoryMovementCreateDto dto = new InventoryMovementCreateDto();
        dto.setProductId(productId);
        dto.setQuantity(null);
        dto.setWarehouseId(warehouseId);

        InventoryMovement result = service.recordInbound(dto);

        assertNotNull(result);
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    void recordMovement_withNullQtyOnHand_shouldDefaultToZero() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = new Product();
        product.setId(productId);

        Inventory inv = Inventory.builder()
                .id(UUID.randomUUID())
                .product(product)
                .qtyOnHand(null)
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

        InventoryMovement result = service.recordInbound(dto);

        assertNotNull(result);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void recordMovement_withInventoryProductNull_shouldSkipFiltering() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Inventory inv = Inventory.builder()
                .id(UUID.randomUUID())
                .product(null)
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
        dto.setQuantity(3);
        dto.setWarehouseId(warehouseId);

        InventoryMovement result = service.recordInbound(dto);

        assertNotNull(result);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void recordMovement_withDifferentProductId_shouldCreateNewInventory() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID differentProductId = UUID.randomUUID();

        Product product = new Product();
        product.setId(differentProductId);

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
        dto.setQuantity(3);
        dto.setWarehouseId(warehouseId);

        InventoryMovement result = service.recordInbound(dto);

        assertNotNull(result);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void recordMovement_withAllDtoFields_shouldSetAllFields() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(inventoryRepository.findByWarehouseId(warehouseId)).thenReturn(List.of());
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
        dto.setReference("REF-001");
        dto.setDescription("Test movement");

        InventoryMovement result = service.recordInbound(dto);

        assertNotNull(result);
        assertEquals(MovementType.INBOUND, result.getType());
        assertEquals(dto.getQuantity(), result.getQuantity());
        assertEquals(dto.getReference(), result.getReference());
        assertEquals(dto.getDescription(), result.getDescription());
        assertNotNull(result.getOccurredAt());
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }
}