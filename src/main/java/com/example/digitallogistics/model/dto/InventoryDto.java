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
public class InventoryDto {
    private UUID id;
    private UUID warehouseId;
    private String warehouseName;
    private UUID productId;
    private String productSku;
    private String productName;
    private Integer qtyOnHand;
    private Integer qtyReserved;
}