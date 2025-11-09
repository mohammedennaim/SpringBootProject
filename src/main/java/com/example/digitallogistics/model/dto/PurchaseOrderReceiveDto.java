package com.example.digitallogistics.model.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public class PurchaseOrderReceiveDto {
    @NotNull
    public List<PurchaseOrderReceiveLineDto> lines;
}
