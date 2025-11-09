package com.example.digitallogistics.model.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class PurchaseOrderReceiveLineDto {
    @NotNull
    public UUID lineId;

    @NotNull
    public Integer receivedQuantity;
}
