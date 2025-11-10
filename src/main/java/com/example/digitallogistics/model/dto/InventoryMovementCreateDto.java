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
public class InventoryMovementCreateDto {
    @NotNull
    private UUID warehouseId;

    @NotNull
    private UUID productId;

    @NotNull
    private Integer quantity;

    private String reference;

    private String description;
}
