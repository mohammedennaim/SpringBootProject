package com.example.digitallogistics.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class PurchaseOrderLineCreateDto {
    @NotNull
    public UUID productId;

    @NotNull
    public Integer quantity;

    public BigDecimal unitPrice;
}
