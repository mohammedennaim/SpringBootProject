package com.example.digitallogistics.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.digitallogistics.model.dto.SalesOrderCreateDto;
import com.example.digitallogistics.model.entity.SalesOrder;
import com.example.digitallogistics.model.entity.SalesOrderLine;
import com.example.digitallogistics.model.enums.OrderStatus;

public interface SalesOrderService {
    List<SalesOrder> findAll(Optional<UUID> clientId, Optional<OrderStatus> status);
    SalesOrder create(SalesOrderCreateDto dto);
    Optional<SalesOrder> findById(UUID id);
    List<SalesOrderLine> findLines(UUID orderId);
    SalesOrder reserve(UUID id);
    SalesOrder ship(UUID id);
    SalesOrder deliver(UUID id);
    SalesOrder cancel(UUID id);
}
