package com.example.digitallogistics.model.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderLineCreateDto {
    @NotNull
    private UUID productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
