package com.example.digitallogistics.service;

import com.example.digitallogistics.model.entity.SalesOrder;
import java.time.LocalDate;
import java.util.UUID;

public interface AdvancedLogisticsService {
    
    boolean reserveStock(SalesOrder salesOrder);
    void releaseExpiredReservations();
    boolean canShipOrder(UUID salesOrderId);
    LocalDate calculateShipmentDate(LocalDate orderDate);
    LocalDate findNextAvailableShipmentSlot(UUID warehouseId, LocalDate fromDate);
    void processBackOrders(UUID productId, UUID warehouseId);
    boolean checkStockAvailability(UUID productId, UUID warehouseId, Integer quantity);
}