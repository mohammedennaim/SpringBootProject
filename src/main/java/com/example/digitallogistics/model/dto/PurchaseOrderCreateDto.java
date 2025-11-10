package com.example.digitallogistics.model.dto;

import java.time.LocalDateTime;
import java.util.List;
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
public class PurchaseOrderCreateDto {
    @NotNull
    private UUID supplierId;

    private LocalDateTime expectedDelivery;

    @NotNull
    private List<PurchaseOrderLineCreateDto> lines;
}
