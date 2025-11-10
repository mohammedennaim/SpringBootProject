package com.example.digitallogistics.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.digitallogistics.model.enums.MovementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovementDto {
    private UUID id;
    private MovementType type;
    private Integer quantity;
    private LocalDateTime occurredAt;
    private String reference;
    private String description;
}
