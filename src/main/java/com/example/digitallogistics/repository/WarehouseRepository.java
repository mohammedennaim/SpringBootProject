package com.example.digitallogistics.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    List<Warehouse> findByCode(String code);
    List<Warehouse> findByActiveTrue();
}
