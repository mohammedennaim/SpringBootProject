package com.example.digitallogistics.service.impl;

import com.example.digitallogistics.model.entity.*;
import com.example.digitallogistics.model.enums.OrderStatus;
import com.example.digitallogistics.repository.*;
import com.example.digitallogistics.service.AdvancedLogisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvancedLogisticsServiceImpl implements AdvancedLogisticsService {

    private final ReservationRepository reservationRepository;
    private final BackOrderRepository backOrderRepository;
    private final ShipmentSlotRepository shipmentSlotRepository;
    private final InventoryRepository inventoryRepository;
    private final SalesOrderLineRepository salesOrderLineRepository;
    private final WarehouseRepository warehouseRepository;

    @Value("${logistics.cutoff.hour:15}")
    private int cutoffHour;

    @Value("${logistics.reservation.ttl.hours:24}")
    private int reservationTtlHours;

    @Value("${logistics.shipment.default.capacity:100}")
    private int defaultShipmentCapacity;

    @Override
    @Transactional
    public boolean reserveStock(SalesOrder salesOrder) {
        List<SalesOrderLine> orderLines = salesOrderLineRepository.findBySalesOrderId(salesOrder.getId());
        
        for (SalesOrderLine line : orderLines) {
            if (!reserveStockForLine(line, salesOrder)) {
                // Rollback des réservations déjà faites
                releaseReservationsForOrder(salesOrder.getId());
                return false;
            }
        }
        
        salesOrder.setStatus(OrderStatus.RESERVED);
        return true;
    }

    private boolean reserveStockForLine(SalesOrderLine line, SalesOrder salesOrder) {
        List<Inventory> inventories = inventoryRepository.findByProductIdOrderByWarehousePriority(line.getProduct().getId());
        int remainingQty = line.getQuantity();
        
        for (Inventory inventory : inventories) {
            if (remainingQty <= 0) break;
            
            int availableQty = inventory.getQtyOnHand() - inventory.getQtyReserved();
            if (availableQty <= 0) continue;
            
            int toReserve = Math.min(availableQty, remainingQty);
            
            // Créer la réservation
            Reservation reservation = Reservation.builder()
                .salesOrder(salesOrder)
                .inventory(inventory)
                .quantity(toReserve)
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(reservationTtlHours))
                .isActive(true)
                .build();
            
            reservationRepository.save(reservation);
            
            // Mettre à jour l'inventaire
            inventory.setQtyReserved(inventory.getQtyReserved() + toReserve);
            inventoryRepository.save(inventory);
            
            remainingQty -= toReserve;
        }
        
        // Créer un back order pour la quantité non disponible
        if (remainingQty > 0) {
            createBackOrder(salesOrder, line.getProduct(), remainingQty);
        }
        
        return true;
    }

    private void createBackOrder(SalesOrder salesOrder, Product product, int quantity) {
        // Trouver l'entrepôt principal (premier dans la liste de priorité)
        List<Inventory> inventories = inventoryRepository.findByProductIdOrderByWarehousePriority(product.getId());
        Warehouse warehouse = inventories.isEmpty() ? null : inventories.get(0).getWarehouse();
        
        BackOrder backOrder = BackOrder.builder()
            .originalOrder(salesOrder)
            .product(product)
            .warehouse(warehouse)
            .quantityNeeded(quantity)
            .build();
        
        backOrderRepository.save(backOrder);
        log.info("Created back order for {} units of product {} for order {}", 
                quantity, product.getSku(), salesOrder.getId());
    }

    @Override
    @Transactional
    public void releaseExpiredReservations() {
        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations(LocalDateTime.now());
        
        for (Reservation reservation : expiredReservations) {
            // Libérer le stock réservé
            Inventory inventory = reservation.getInventory();
            inventory.setQtyReserved(inventory.getQtyReserved() - reservation.getQuantity());
            inventoryRepository.save(inventory);
            
            // Marquer la réservation comme inactive
            reservation.setIsActive(false);
            reservationRepository.save(reservation);
            
            log.info("Released expired reservation {} for {} units", 
                    reservation.getId(), reservation.getQuantity());
        }
    }

    @Override
    public boolean canShipOrder(UUID salesOrderId) {
        List<Reservation> reservations = reservationRepository.findBySalesOrderIdAndIsActiveTrue(salesOrderId);
        return !reservations.isEmpty();
    }

    @Override
    public LocalDate calculateShipmentDate(LocalDate orderDate) {
        LocalTime currentTime = LocalTime.now();
        
        if (currentTime.getHour() >= cutoffHour) {
            // Commande après cut-off, planifier pour le jour ouvré suivant
            LocalDate nextDay = orderDate.plusDays(1);
            // Éviter les week-ends (simple implémentation)
            while (nextDay.getDayOfWeek().getValue() > 5) {
                nextDay = nextDay.plusDays(1);
            }
            return nextDay;
        }
        
        return orderDate;
    }

    @Override
    @Transactional
    public LocalDate findNextAvailableShipmentSlot(UUID warehouseId, LocalDate fromDate) {
        List<ShipmentSlot> availableSlots = shipmentSlotRepository.findAvailableSlots(warehouseId, fromDate);
        
        if (!availableSlots.isEmpty()) {
            ShipmentSlot slot = availableSlots.get(0);
            slot.setCurrentUsage(slot.getCurrentUsage() + 1);
            shipmentSlotRepository.save(slot);
            return slot.getSlotDate();
        }
        
        // Créer un nouveau créneau si aucun n'existe
        LocalDate nextDate = fromDate;
        while (nextDate.getDayOfWeek().getValue() > 5) {
            nextDate = nextDate.plusDays(1);
        }
        
        ShipmentSlot newSlot = ShipmentSlot.builder()
            .warehouse(warehouseRepository.findById(warehouseId).orElse(null))
            .slotDate(nextDate)
            .maxCapacity(defaultShipmentCapacity)
            .currentUsage(1)
            .build();
        
        shipmentSlotRepository.save(newSlot);
        return nextDate;
    }

    @Override
    @Transactional
    public void processBackOrders(UUID productId, UUID warehouseId) {
        List<BackOrder> backOrders = backOrderRepository.findByProductIdAndWarehouseIdAndIsFulfilledFalse(productId, warehouseId);
        
        for (BackOrder backOrder : backOrders) {
            if (checkStockAvailability(productId, warehouseId, backOrder.getQuantityNeeded())) {
                // Essayer de réserver le stock pour le back order
                if (reserveStockForBackOrder(backOrder)) {
                    backOrder.setIsFulfilled(true);
                    backOrder.setFulfilledAt(LocalDateTime.now());
                    backOrderRepository.save(backOrder);
                    
                    log.info("Fulfilled back order {} for {} units", 
                            backOrder.getId(), backOrder.getQuantityNeeded());
                }
            }
        }
    }

    private boolean reserveStockForBackOrder(BackOrder backOrder) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductIdAndWarehouseId(
            backOrder.getProduct().getId(), backOrder.getWarehouse().getId());
        
        if (inventoryOpt.isEmpty()) return false;
        
        Inventory inventory = inventoryOpt.get();
        int availableQty = inventory.getQtyOnHand() - inventory.getQtyReserved();
        
        if (availableQty >= backOrder.getQuantityNeeded()) {
            inventory.setQtyReserved(inventory.getQtyReserved() + backOrder.getQuantityNeeded());
            inventoryRepository.save(inventory);
            return true;
        }
        
        return false;
    }

    @Override
    public boolean checkStockAvailability(UUID productId, UUID warehouseId, Integer quantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId);
        
        if (inventoryOpt.isEmpty()) return false;
        
        Inventory inventory = inventoryOpt.get();
        int availableQty = inventory.getQtyOnHand() - inventory.getQtyReserved();
        
        return availableQty >= quantity;
    }

    private void releaseReservationsForOrder(UUID salesOrderId) {
        List<Reservation> reservations = reservationRepository.findBySalesOrderIdAndIsActiveTrue(salesOrderId);
        
        for (Reservation reservation : reservations) {
            Inventory inventory = reservation.getInventory();
            inventory.setQtyReserved(inventory.getQtyReserved() - reservation.getQuantity());
            inventoryRepository.save(inventory);
            
            reservation.setIsActive(false);
            reservationRepository.save(reservation);
        }
    }
}