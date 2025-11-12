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
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    List<Shipment> findByOrderId(UUID orderId);
    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);
    Page<Shipment> findByWarehouseId(UUID warehouseId, Pageable pageable);
    Page<Shipment> findByCarrierId(UUID carrierId, Pageable pageable);
    boolean existsByOrderId(UUID orderId);

    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.status = :status")
    long countByStatus(@Param("status") ShipmentStatus status);
    
    @Query("SELECT s FROM Shipment s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:warehouseId IS NULL OR s.warehouse.id = :warehouseId)")
    Page<Shipment> findByStatusAndWarehouseId(
        @Param("status") ShipmentStatus status,
        @Param("warehouseId") UUID warehouseId,
        Pageable pageable
    );
}
