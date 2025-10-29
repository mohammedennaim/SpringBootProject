package com.example.digitallogistics.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByNameContainingIgnoreCase(String namePart);
}
