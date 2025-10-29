package com.example.digitallogistics.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.SalesOrder;
import com.example.digitallogistics.model.enums.OrderStatus;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    List<SalesOrder> findByClientId(Long id);
    List<SalesOrder> findByStatus(OrderStatus status);
}
