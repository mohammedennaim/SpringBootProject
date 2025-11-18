package com.example.digitallogistics.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private UUID id;
    private String sku;
    private String name;
    private String category;
    private BigDecimal unitPrice;
    private BigDecimal profit;
    private boolean active;
    private String image;
}
