package com.example.digitallogistics.model.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SalesOrderLineCreateDto {
    @NotNull
    public UUID productId;

    @NotNull
    @Min(1)
    public Integer quantity;
}
