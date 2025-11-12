package com.example.digitallogistics.model.dto;

import java.math.BigDecimal;
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
public class PurchaseOrderLineCreateDto {
    @NotNull
    private UUID productId;
    @NotNull
    private Integer quantity;
    private BigDecimal unitPrice;
}
