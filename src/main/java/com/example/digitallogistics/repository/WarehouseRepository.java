package com.example.digitallogistics.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    List<Warehouse> findByCode(String code);
    List<Warehouse> findByActiveTrue();
}
