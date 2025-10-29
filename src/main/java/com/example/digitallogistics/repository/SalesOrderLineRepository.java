package com.example.digitallogistics.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.SalesOrderLine;

public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, Long> {
    List<SalesOrderLine> findBySalesOrderId(Long id);
    List<SalesOrderLine> findByProductId(Long productId);
}
