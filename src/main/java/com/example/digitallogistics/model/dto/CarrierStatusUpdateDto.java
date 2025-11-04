package com.example.digitallogistics.model.dto;

import com.example.digitallogistics.model.enums.CarrierStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarrierStatusUpdateDto {
    
    @NotNull(message = "Status is required")
    private CarrierStatus status;
}