package com.example.digitallogistics.repository;

import java.util.List;
import java.util.UUID;

import com.example.digitallogistics.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.digitallogistics.model.entity.SalesOrder;
import com.example.digitallogistics.model.enums.OrderStatus;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
    List<SalesOrder> findByClientId(UUID id);
    List<SalesOrder> findByStatus(OrderStatus status);
}
