package com.example.digitallogistics.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.digitallogistics.model.enums.ShipmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDto {
    
    private UUID id;
    private UUID orderId;
    private WarehouseDto warehouse;
    private CarrierDto carrier;
    private String trackingNumber;
    private ShipmentStatus status;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
}