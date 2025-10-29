package com.example.digitallogistics.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.PurchaseOrderLine;

public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine, Long> {
    List<PurchaseOrderLine> findByPurchaseOrderId(Long id);
    List<PurchaseOrderLine> findByProductId(Long id);
}
