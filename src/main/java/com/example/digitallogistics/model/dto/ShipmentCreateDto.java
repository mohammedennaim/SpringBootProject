package com.example.digitallogistics.model.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentCreateDto {
    
    @NotNull(message = "Order ID is required")
    private UUID orderId;
    
    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;
    
    private UUID carrierId;
    private String trackingNumber;
}