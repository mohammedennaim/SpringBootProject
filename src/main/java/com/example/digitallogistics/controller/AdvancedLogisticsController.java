package com.example.digitallogistics.controller;

import com.example.digitallogistics.model.entity.BackOrder;
import com.example.digitallogistics.repository.BackOrderRepository;
import com.example.digitallogistics.service.AdvancedLogisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/advanced-logistics")
@RequiredArgsConstructor
@Tag(name = "Advanced Logistics", description = "Règles métier avancées")
public class AdvancedLogisticsController {

    private final AdvancedLogisticsService advancedLogisticsService;
    private final BackOrderRepository backOrderRepository;

    @PostMapping("/reservations/cleanup")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Nettoyer les réservations expirées")
    public ResponseEntity<String> cleanupExpiredReservations() {
        advancedLogisticsService.releaseExpiredReservations();
        return ResponseEntity.ok("Expired reservations cleaned up successfully");
    }

    @GetMapping("/orders/{orderId}/can-ship")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Vérifier si une commande peut être expédiée")
    public ResponseEntity<Boolean> canShipOrder(@PathVariable UUID orderId) {
        try {
            boolean canShip = advancedLogisticsService.canShipOrder(orderId);
            return ResponseEntity.ok(canShip);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/shipment-date")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Calculer la date d'expédition selon le cut-off")
    public ResponseEntity<LocalDate> calculateShipmentDate(@RequestParam LocalDate orderDate) {
        LocalDate shipmentDate = advancedLogisticsService.calculateShipmentDate(orderDate);
        return ResponseEntity.ok(shipmentDate);
    }

    @GetMapping("/warehouses/{warehouseId}/next-slot")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Trouver le prochain créneau d'expédition disponible")
    public ResponseEntity<LocalDate> findNextAvailableSlot(
            @PathVariable UUID warehouseId,
            @RequestParam LocalDate fromDate) {
        LocalDate nextSlot = advancedLogisticsService.findNextAvailableShipmentSlot(warehouseId, fromDate);
        return ResponseEntity.ok(nextSlot);
    }

    @PostMapping("/products/{productId}/warehouses/{warehouseId}/process-backorders")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Traiter les back orders pour un produit")
    public ResponseEntity<String> processBackOrders(
            @PathVariable UUID productId,
            @PathVariable UUID warehouseId) {
        advancedLogisticsService.processBackOrders(productId, warehouseId);
        return ResponseEntity.ok("Back orders processed successfully");
    }

    @GetMapping("/stock-availability")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Vérifier la disponibilité du stock")
    public ResponseEntity<Boolean> checkStockAvailability(
            @RequestParam UUID productId,
            @RequestParam UUID warehouseId,
            @RequestParam Integer quantity) {
        boolean available = advancedLogisticsService.checkStockAvailability(productId, warehouseId, quantity);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/back-orders")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Lister tous les back orders non traités")
    public ResponseEntity<List<BackOrder>> getBackOrders() {
        List<BackOrder> backOrders = backOrderRepository.findByIsFulfilledFalse();
        return ResponseEntity.ok(backOrders);
    }
}