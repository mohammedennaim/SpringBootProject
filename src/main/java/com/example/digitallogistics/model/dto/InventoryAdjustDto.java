package com.example.digitallogistics.model.dto;

import java.util.UUID;

public class InventoryAdjustDto {
    private UUID warehouseId;
    private UUID productId;
    private Integer adjustmentQty; // positive for increase, negative for decrease
    private String reason;

    public InventoryAdjustDto() {
    }

    public InventoryAdjustDto(UUID warehouseId, UUID productId, Integer adjustmentQty, String reason) {
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.adjustmentQty = adjustmentQty;
        this.reason = reason;
    }

    public UUID getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getAdjustmentQty() {
        return adjustmentQty;
    }

    public void setAdjustmentQty(Integer adjustmentQty) {
        this.adjustmentQty = adjustmentQty;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}