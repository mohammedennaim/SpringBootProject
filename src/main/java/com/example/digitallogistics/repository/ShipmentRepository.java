package com.example.digitallogistics.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    List<Shipment> findByStatus(String status);
}
