package com.example.digitallogistics.model.dto;

import com.example.digitallogistics.model.enums.ShipmentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentStatusUpdateDto {
    
    @NotNull(message = "Status is required")
    private ShipmentStatus status;
}