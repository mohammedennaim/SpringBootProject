package com.example.digitallogistics.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.digitallogistics.model.dto.PurchaseOrderCreateDto;
import com.example.digitallogistics.model.dto.PurchaseOrderReceiveDto;
import com.example.digitallogistics.model.entity.PurchaseOrder;
import com.example.digitallogistics.model.enums.PurchaseOrderStatus;

public interface PurchaseOrderService {
    List<PurchaseOrder> findAll(Optional<UUID> supplierId, Optional<PurchaseOrderStatus> status);
    PurchaseOrder create(PurchaseOrderCreateDto dto);
    Optional<PurchaseOrder> findById(UUID id);
    PurchaseOrder approve(UUID id);
    PurchaseOrder receive(UUID id, PurchaseOrderReceiveDto dto,UUID warehouseId);
    PurchaseOrder cancel(UUID id);
}
