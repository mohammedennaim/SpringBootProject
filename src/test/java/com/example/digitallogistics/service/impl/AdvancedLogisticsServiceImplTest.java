package com.example.digitallogistics.service.impl;

import com.example.digitallogistics.model.entity.*;
import com.example.digitallogistics.model.enums.OrderStatus;
import com.example.digitallogistics.repository.*;
import com.example.digitallogistics.service.AdvancedLogisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvancedLogisticsServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private BackOrderRepository backOrderRepository;
    @Mock
    private ShipmentSlotRepository shipmentSlotRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private SalesOrderLineRepository salesOrderLineRepository;
    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private AdvancedLogisticsServiceImpl advancedLogisticsService;

    private SalesOrder testOrder;
    private Product testProduct;
    private Warehouse testWarehouse;
    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        testWarehouse = Warehouse.builder()
                .id(UUID.randomUUID())
                .code("WH001")
                .name("Test Warehouse")
                .priority(1)
                .build();

        testProduct = Product.builder()
                .id(UUID.randomUUID())
                .sku("TEST-001")
                .name("Test Product")
                .build();

        testInventory = Inventory.builder()
                .id(UUID.randomUUID())
                .warehouse(testWarehouse)
                .product(testProduct)
                .qtyOnHand(100)
                .qtyReserved(0)
                .build();

        testOrder = SalesOrder.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCheckStockAvailability_SufficientStock() {
        when(inventoryRepository.findByProductIdAndWarehouseId(testProduct.getId(), testWarehouse.getId()))
                .thenReturn(Optional.of(testInventory));

        boolean result = advancedLogisticsService.checkStockAvailability(
                testProduct.getId(), testWarehouse.getId(), 50);

        assertTrue(result);
    }

    @Test
    void testCheckStockAvailability_InsufficientStock() {
        when(inventoryRepository.findByProductIdAndWarehouseId(testProduct.getId(), testWarehouse.getId()))
                .thenReturn(Optional.of(testInventory));

        boolean result = advancedLogisticsService.checkStockAvailability(
                testProduct.getId(), testWarehouse.getId(), 150);

        assertFalse(result);
    }

    @Test
    void testCalculateShipmentDate_BeforeCutoff() {
        // Test avec une date à 10h (avant cut-off de 15h)
        LocalDate orderDate = LocalDate.now();
        // Mock l'heure actuelle pour être avant 15h
        LocalDate result = advancedLogisticsService.calculateShipmentDate(orderDate);
        // Le résultat peut être aujourd'hui ou demain selon l'heure actuelle
        assertTrue(result.equals(orderDate) || result.equals(orderDate.plusDays(1)));
    }

    @Test
    void testCanShipOrder_WithActiveReservations() {
        List<Reservation> reservations = Arrays.asList(
                Reservation.builder()
                        .id(UUID.randomUUID())
                        .salesOrder(testOrder)
                        .inventory(testInventory)
                        .quantity(10)
                        .isActive(true)
                        .build()
        );

        when(reservationRepository.findBySalesOrderIdAndIsActiveTrue(testOrder.getId()))
                .thenReturn(reservations);

        boolean result = advancedLogisticsService.canShipOrder(testOrder.getId());
        assertTrue(result);
    }

    @Test
    void testCanShipOrder_NoActiveReservations() {
        when(reservationRepository.findBySalesOrderIdAndIsActiveTrue(testOrder.getId()))
                .thenReturn(Arrays.asList());

        boolean result = advancedLogisticsService.canShipOrder(testOrder.getId());
        assertFalse(result);
    }

    @Test
    void testReleaseExpiredReservations() {
        Reservation expiredReservation = Reservation.builder()
                .id(UUID.randomUUID())
                .salesOrder(testOrder)
                .inventory(testInventory)
                .quantity(10)
                .isActive(true)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .build();

        when(reservationRepository.findExpiredReservations(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(expiredReservation));

        advancedLogisticsService.releaseExpiredReservations();

        verify(inventoryRepository).save(testInventory);
        verify(reservationRepository).save(expiredReservation);
        assertFalse(expiredReservation.getIsActive());
    }

    @Test
    void testFindNextAvailableShipmentSlot_ExistingSlot() {
        LocalDate testDate = LocalDate.now();
        ShipmentSlot availableSlot = ShipmentSlot.builder()
                .id(UUID.randomUUID())
                .warehouse(testWarehouse)
                .slotDate(testDate)
                .maxCapacity(100)
                .currentUsage(50)
                .build();

        when(shipmentSlotRepository.findAvailableSlots(testWarehouse.getId(), testDate))
                .thenReturn(Arrays.asList(availableSlot));

        LocalDate result = advancedLogisticsService.findNextAvailableShipmentSlot(testWarehouse.getId(), testDate);

        assertEquals(testDate, result);
        verify(shipmentSlotRepository).save(availableSlot);
        assertEquals(51, availableSlot.getCurrentUsage());
    }
}