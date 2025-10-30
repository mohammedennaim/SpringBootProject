package com.example.digitallogistics.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.SalesOrderLine;

public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, UUID> {
    List<SalesOrderLine> findBySalesOrderId(UUID id);
    List<SalesOrderLine> findByProductId(UUID productId);
}
