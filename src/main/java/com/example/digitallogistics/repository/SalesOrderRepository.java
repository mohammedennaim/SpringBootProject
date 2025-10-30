package com.example.digitallogistics.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.digitallogistics.model.entity.SalesOrder;
import com.example.digitallogistics.model.enums.OrderStatus;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
    // Client id is UUID in the domain model — accept UUID here to match entity
    List<SalesOrder> findByClientId(UUID id);
    List<SalesOrder> findByStatus(OrderStatus status);
}
