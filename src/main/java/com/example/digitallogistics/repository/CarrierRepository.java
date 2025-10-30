package com.example.digitallogistics.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.Carrier;
import com.example.digitallogistics.model.enums.CarrierStatus;

public interface CarrierRepository extends JpaRepository<Carrier, UUID> {
    List<Carrier> findByCode(String code);
    List<Carrier> findByStatus(CarrierStatus status);
}
