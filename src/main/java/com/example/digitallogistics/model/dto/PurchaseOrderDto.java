package com.example.digitallogistics.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.digitallogistics.model.enums.PurchaseOrderStatus;

public class PurchaseOrderDto {
    private UUID id;
    private SupplierDto supplier;
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expectedDelivery;
    private List<PurchaseOrderLineDto> lines;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SupplierDto getSupplier() { return supplier; }
    public void setSupplier(SupplierDto supplier) { this.supplier = supplier; }
    public PurchaseOrderStatus getStatus() { return status; }
    public void setStatus(PurchaseOrderStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getExpectedDelivery() { return expectedDelivery; }
    public void setExpectedDelivery(LocalDateTime expectedDelivery) { this.expectedDelivery = expectedDelivery; }
    public List<PurchaseOrderLineDto> getLines() { return lines; }
    public void setLines(List<PurchaseOrderLineDto> lines) { this.lines = lines; }
}
