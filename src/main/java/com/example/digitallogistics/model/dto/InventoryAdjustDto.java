package com.example.digitallogistics.model.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAdjustDto {
    private UUID warehouseId;
    private UUID productId;
    private Integer adjustmentQty;
    private String reason;
}