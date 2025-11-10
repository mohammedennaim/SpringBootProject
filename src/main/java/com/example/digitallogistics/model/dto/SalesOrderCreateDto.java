package com.example.digitallogistics.model.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderCreateDto {
    @NotNull
    private UUID clientId;

    @NotEmpty
    private List<SalesOrderLineCreateDto> lines;
}
