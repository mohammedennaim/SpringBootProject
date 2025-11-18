package com.example.digitallogistics.repository;

import com.example.digitallogistics.model.entity.ShipmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShipmentSlotRepository extends JpaRepository<ShipmentSlot, UUID> {
    
    Optional<ShipmentSlot> findByWarehouseIdAndSlotDate(UUID warehouseId, LocalDate slotDate);
    
    @Query("SELECT s FROM ShipmentSlot s WHERE s.warehouse.id = :warehouseId AND s.slotDate >= :fromDate AND s.currentUsage < s.maxCapacity ORDER BY s.slotDate")
    List<ShipmentSlot> findAvailableSlots(@Param("warehouseId") UUID warehouseId, @Param("fromDate") LocalDate fromDate);
}