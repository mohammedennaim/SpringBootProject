package com.example.digitallogistics.model.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SalesOrderCreateDto {
    @NotNull
    public UUID clientId;

    @NotEmpty
    public List<SalesOrderLineCreateDto> lines;
}
