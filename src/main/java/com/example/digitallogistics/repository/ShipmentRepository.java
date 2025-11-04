package com.example.digitallogistics.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.digitallogistics.model.entity.Shipment;
import com.example.digitallogistics.model.enums.ShipmentStatus;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    // Recherche par numéro de suivi
    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    // Recherche par commande
    List<Shipment> findByOrderId(UUID orderId);

    // Recherche par statut
    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);

    // Recherche par entrepôt
    Page<Shipment> findByWarehouseId(UUID warehouseId, Pageable pageable);

    // Recherche par transporteur
    Page<Shipment> findByCarrierId(UUID carrierId, Pageable pageable);

    // Vérifier si une commande a déjà une expédition
    boolean existsByOrderId(UUID orderId);

    // Compter les expéditions par statut
    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.status = :status")
    long countByStatus(@Param("status") ShipmentStatus status);

    // Recherche combinée par statut et entrepôt
    @Query("SELECT s FROM Shipment s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:warehouseId IS NULL OR s.warehouse.id = :warehouseId)")
    Page<Shipment> findByStatusAndWarehouseId(
        @Param("status") ShipmentStatus status,
        @Param("warehouseId") UUID warehouseId,
        Pageable pageable);
}
