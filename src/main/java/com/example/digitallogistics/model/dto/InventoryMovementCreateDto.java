package com.example.digitallogistics.model.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class InventoryMovementCreateDto {
    @NotNull
    public UUID warehouseId;

    @NotNull
    public UUID productId;

    @NotNull
    public Integer quantity;

    public String reference;

    public String description;
}
