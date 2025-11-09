package com.example.digitallogistics.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class PurchaseOrderCreateDto {
    @NotNull
    public UUID supplierId;

    public LocalDateTime expectedDelivery;

    @NotNull
    public List<PurchaseOrderLineCreateDto> lines;
}
