package com.example.digitallogistics.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.PurchaseOrder;
import com.example.digitallogistics.model.enums.PurchaseOrderStatus;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplierId(Long id);
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);
}
