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

import com.example.digitallogistics.model.entity.Carrier;
import com.example.digitallogistics.model.enums.CarrierStatus;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, UUID> {
    Optional<Carrier> findByCode(String code);

    boolean existsByCode(String code);

    Page<Carrier> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Carrier> findByStatus(CarrierStatus status, Pageable pageable);

    @Query("SELECT c FROM Carrier c WHERE c.status = 'ACTIVE'")
    List<Carrier> findAllActive();

    @Query("SELECT c FROM Carrier c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Carrier> findByStatusAndNameContaining(
        @Param("status") CarrierStatus status,
        @Param("name") String name,
        Pageable pageable);

    long countByStatus(CarrierStatus status);

    @Query("SELECT c FROM Carrier c WHERE c.maxDailyShipments >= :minCapacity AND c.status = 'ACTIVE'")
    List<Carrier> findByMinCapacity(@Param("minCapacity") Integer minCapacity);

    @Query("SELECT c FROM Carrier c WHERE " +
           "c.rate BETWEEN :minRate AND :maxRate AND " +
           "c.status = 'ACTIVE' " +
           "ORDER BY c.rate ASC")
    List<Carrier> findByRateRange(
        @Param("minRate") java.math.BigDecimal minRate,
        @Param("maxRate") java.math.BigDecimal maxRate);
}
