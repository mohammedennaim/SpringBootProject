package com.example.digitallogistics.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.model.entity.Warehouse;
import com.example.digitallogistics.repository.InventoryRepository;
import com.example.digitallogistics.repository.ProductRepository;
import com.example.digitallogistics.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    InventoryRepository inventoryRepository;
    @Mock
    WarehouseRepository warehouseRepository;
    @Mock
    ProductRepository productRepository;

    @InjectMocks
    InventoryServiceImpl service;

    @Test
    void adjustInventory_existingInventory_updatesQty() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        
        Product product = new Product();
        product.setId(productId);
        
        Warehouse warehouse = Warehouse.builder().id(warehouseId).build();
        
        Inventory inv = Inventory.builder()
                .id(UUID.randomUUID())
                .warehouse(warehouse)
                .product(product)
                .qtyOnHand(5)
                .qtyReserved(0)
                .build();
        when(inventoryRepository.findAll()).thenReturn(List.of(inv));
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Inventory res = service.adjustInventory(warehouseId, productId, 3, "test");
        assertEquals(8, res.getQtyOnHand());
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void adjustInventory_createNew_whenMissing() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(inventoryRepository.findAll()).thenReturn(List.of());

        Warehouse wh = Warehouse.builder().id(warehouseId).code("MAIN").build();
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(wh));
        
        Product p = new Product();
        p.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(p));

        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Inventory res = service.adjustInventory(warehouseId, productId, 4, "init");
        assertEquals(4, res.getQtyOnHand());
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void getAvailableQuantity_shouldReturnSumOfAvailableAcrossWarehouses() {
        UUID productId = UUID.randomUUID();
        Product p = new Product();
        p.setId(productId);
        
        Inventory inv1 = Inventory.builder()
                .id(UUID.randomUUID())
                .product(p)
                .qtyOnHand(10)
                .qtyReserved(3)
                .build();
        
        Inventory inv2 = Inventory.builder()
                .id(UUID.randomUUID())
                .product(p)
                .qtyOnHand(8)
                .qtyReserved(2)
                .build();

        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of(inv1, inv2));

        Integer result = service.getAvailableQuantity(productId);

        // (10-3) + (8-2) = 7 + 6 = 13
        assertEquals(13, result);
    }

    @Test
    void findByWarehouseId_shouldReturnInventoriesInWarehouse() {
        UUID warehouseId = UUID.randomUUID();
        Inventory inv = Inventory.builder().id(UUID.randomUUID()).qtyOnHand(5).build();
        
        when(inventoryRepository.findByWarehouseId(warehouseId)).thenReturn(List.of(inv));

        List<Inventory> result = service.findByWarehouseId(warehouseId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(inventoryRepository).findByWarehouseId(warehouseId);
    }

    @Test
    void updateInventory_shouldCreateNewWhenNotExists() {
        UUID invId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(inventoryRepository.findById(invId)).thenReturn(Optional.empty());
        
        Warehouse wh = Warehouse.builder().id(warehouseId).build();
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(wh));
        
        Product p = new Product();
        p.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(p));
        
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Inventory result = service.updateInventory(invId, warehouseId, productId, 10, 2);

        assertNotNull(result);
        assertEquals(10, result.getQtyOnHand());
        assertEquals(2, result.getQtyReserved());
        verify(inventoryRepository).save(any(Inventory.class));
    }
}
