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

    @Test
    void getAvailableQuantityInWarehouse_shouldReturnAvailable() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Warehouse wh = Warehouse.builder().id(warehouseId).build();
        Product p = new Product();
        p.setId(productId);
        Inventory inv = Inventory.builder().warehouse(wh).product(p).qtyOnHand(10).qtyReserved(3).build();
        when(inventoryRepository.findAll()).thenReturn(List.of(inv));
        assertEquals(7, service.getAvailableQuantityInWarehouse(warehouseId, productId));
    }

    @Test
    void adjustProductTotal_shouldIncreaseInMain() {
        UUID productId = UUID.randomUUID();
        UUID mainId = UUID.randomUUID();
        Warehouse main = Warehouse.builder().id(mainId).code("MAIN").build();
        Product p = new Product();
        p.setId(productId);
        Inventory inv = Inventory.builder().warehouse(main).product(p).qtyOnHand(5).qtyReserved(0).build();
        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of(inv));
        when(warehouseRepository.findByCode("MAIN")).thenReturn(List.of(main));
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        service.adjustProductTotal(productId, 10);
        verify(inventoryRepository).save(any());
    }

    @Test
    void adjustProductTotal_shouldDecrease() {
        UUID productId = UUID.randomUUID();
        Product p = new Product();
        p.setId(productId);
        Inventory inv = Inventory.builder().product(p).qtyOnHand(10).qtyReserved(2).build();
        java.util.List<Inventory> list = new java.util.ArrayList<>();
        list.add(inv);
        when(inventoryRepository.findByProductId(productId)).thenReturn(list);
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        service.adjustProductTotal(productId, 5);
        verify(inventoryRepository).save(any());
    }

    @Test
    void updateInventory_shouldUpdateExisting() {
        UUID invId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Warehouse wh = Warehouse.builder().id(warehouseId).build();
        Product p = new Product();
        p.setId(productId);
        Inventory inv = Inventory.builder().id(invId).warehouse(wh).product(p).qtyOnHand(5).qtyReserved(1).build();
        when(inventoryRepository.findById(invId)).thenReturn(Optional.of(inv));
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(wh));
        when(productRepository.findById(productId)).thenReturn(Optional.of(p));
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Inventory result = service.updateInventory(invId, warehouseId, productId, 15, 3);
        assertEquals(15, result.getQtyOnHand());
        assertEquals(3, result.getQtyReserved());
    }

    @Test
    void findAll_shouldReturnAllInventories() {
        Inventory inv = Inventory.builder().id(UUID.randomUUID()).qtyOnHand(10).build();
        when(inventoryRepository.findAll()).thenReturn(List.of(inv));
        List<Inventory> result = service.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void findByProductId_shouldReturnInventories() {
        UUID productId = UUID.randomUUID();
        Inventory inv = Inventory.builder().id(UUID.randomUUID()).qtyOnHand(5).build();
        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of(inv));
        List<Inventory> result = service.findByProductId(productId);
        assertEquals(1, result.size());
    }

    @Test
    void findById_shouldReturnInventory() {
        UUID invId = UUID.randomUUID();
        Inventory inv = Inventory.builder().id(invId).qtyOnHand(5).build();
        when(inventoryRepository.findById(invId)).thenReturn(Optional.of(inv));
        Optional<Inventory> result = service.findById(invId);
        assertTrue(result.isPresent());
    }

    @Test
    void findByWarehouseAndProduct_shouldReturnInventory() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Warehouse wh = Warehouse.builder().id(warehouseId).build();
        Product p = new Product();
        p.setId(productId);
        Inventory inv = Inventory.builder().warehouse(wh).product(p).qtyOnHand(5).build();
        when(inventoryRepository.findAll()).thenReturn(List.of(inv));
        Optional<Inventory> result = service.findByWarehouseAndProduct(warehouseId, productId);
        assertTrue(result.isPresent());
    }

    @Test
    void adjustInventory_shouldThrowException_whenWarehouseNotFound() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(inventoryRepository.findAll()).thenReturn(List.of());
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.adjustInventory(warehouseId, productId, 5, "test"));
    }

    @Test
    void adjustProductTotal_shouldThrowException_whenBelowReserved() {
        UUID productId = UUID.randomUUID();
        Product p = new Product();
        p.setId(productId);
        Inventory inv = Inventory.builder().product(p).qtyOnHand(10).qtyReserved(8).build();
        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of(inv));
        assertThrows(RuntimeException.class, () -> service.adjustProductTotal(productId, 5));
    }

    @Test
    void adjustProductTotal_shouldCreateNewInMain_whenNoInventory() {
        UUID productId = UUID.randomUUID();
        UUID mainId = UUID.randomUUID();
        Warehouse main = Warehouse.builder().id(mainId).code("MAIN").build();
        Product p = new Product();
        p.setId(productId);
        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of());
        when(warehouseRepository.findByCode("MAIN")).thenReturn(List.of(main));
        when(productRepository.findById(productId)).thenReturn(Optional.of(p));
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        service.adjustProductTotal(productId, 10);
        verify(inventoryRepository).save(any());
    }

    @Test
    void adjustProductTotal_shouldAddToFirstWarehouse_whenNoMain() {
        UUID productId = UUID.randomUUID();
        UUID whId = UUID.randomUUID();
        Warehouse wh = Warehouse.builder().id(whId).code("WH1").build();
        Product p = new Product();
        p.setId(productId);
        Inventory inv = Inventory.builder().warehouse(wh).product(p).qtyOnHand(5).qtyReserved(0).build();
        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of(inv));
        when(warehouseRepository.findByCode("MAIN")).thenReturn(List.of());
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        service.adjustProductTotal(productId, 10);
        verify(inventoryRepository).save(any());
    }

    @Test
    void adjustProductTotal_shouldThrowException_whenNoWarehouse() {
        UUID productId = UUID.randomUUID();
        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of());
        when(warehouseRepository.findByCode("MAIN")).thenReturn(List.of());
        assertThrows(RuntimeException.class, () -> service.adjustProductTotal(productId, 10));
    }

    @Test
    void adjustProductTotal_shouldThrowException_whenProductNotFound() {
        UUID productId = UUID.randomUUID();
        UUID mainId = UUID.randomUUID();
        Warehouse main = Warehouse.builder().id(mainId).code("MAIN").build();
        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of());
        when(warehouseRepository.findByCode("MAIN")).thenReturn(List.of(main));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.adjustProductTotal(productId, 10));
    }



    @Test
    void adjustProductTotal_shouldThrowException_whenCannotReduceEnough() {
        UUID productId = UUID.randomUUID();
        Product p = new Product();
        p.setId(productId);
        Inventory inv = Inventory.builder().product(p).qtyOnHand(10).qtyReserved(9).build();
        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of(inv));
        assertThrows(RuntimeException.class, () -> service.adjustProductTotal(productId, 5));
    }

    @Test
    void updateInventory_shouldThrowException_whenWarehouseNotFound() {
        UUID invId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Inventory inv = Inventory.builder().id(invId).qtyOnHand(5).build();
        when(inventoryRepository.findById(invId)).thenReturn(Optional.of(inv));
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.updateInventory(invId, warehouseId, productId, 10, 2));
    }

    @Test
    void updateInventory_shouldThrowException_whenProductNotFound() {
        UUID invId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Warehouse wh = Warehouse.builder().id(warehouseId).build();
        Inventory inv = Inventory.builder().id(invId).warehouse(wh).qtyOnHand(5).build();
        when(inventoryRepository.findById(invId)).thenReturn(Optional.of(inv));
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(wh));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.updateInventory(invId, warehouseId, productId, 10, 2));
    }
}
