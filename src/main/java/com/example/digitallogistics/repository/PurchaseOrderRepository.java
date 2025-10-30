package com.example.digitallogistics.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.PurchaseOrder;
import com.example.digitallogistics.model.enums.PurchaseOrderStatus;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    List<PurchaseOrder> findBySupplierId(UUID id);
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);
}
