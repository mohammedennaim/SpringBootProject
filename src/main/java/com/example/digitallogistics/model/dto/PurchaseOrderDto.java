package com.example.digitallogistics.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.digitallogistics.model.enums.PurchaseOrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDto {
    private UUID id;
    private SupplierDto supplier;
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expectedDelivery;
    private List<PurchaseOrderLineDto> lines;
}
