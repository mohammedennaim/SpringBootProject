package com.example.digitallogistics.repository;

import com.example.digitallogistics.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    
    @Query("SELECT r FROM Reservation r WHERE r.expiresAt < :now AND r.isActive = true")
    List<Reservation> findExpiredReservations(@Param("now") LocalDateTime now);
    
    List<Reservation> findBySalesOrderIdAndIsActiveTrue(UUID salesOrderId);
    
    @Query("SELECT r FROM Reservation r WHERE r.inventory.id = :inventoryId AND r.isActive = true")
    List<Reservation> findActiveReservationsByInventoryId(@Param("inventoryId") UUID inventoryId);
}