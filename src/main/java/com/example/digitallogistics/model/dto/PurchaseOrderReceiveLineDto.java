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
public class PurchaseOrderReceiveLineDto {
    @NotNull
    private UUID lineId;
    @NotNull
    private Integer receivedQuantity;
}
