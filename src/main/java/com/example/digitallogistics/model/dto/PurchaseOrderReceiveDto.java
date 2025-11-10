package com.example.digitallogistics.model.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderReceiveDto {
    @NotNull
    private List<PurchaseOrderReceiveLineDto> lines;
}
