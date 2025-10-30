package com.example.digitallogistics.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.PurchaseOrderLine;

public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine, UUID> {
    List<PurchaseOrderLine> findByPurchaseOrderId(UUID id);
    List<PurchaseOrderLine> findByProductId(UUID id);
}
