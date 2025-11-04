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
public class CarrierDto {
    
    private UUID id;
    private String code;
    private String name;
    private String contact;
    private Integer maxDailyShipments;
    private boolean active;
}