package com.example.digitallogistics.model.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateDto {

    @NotBlank(message = "SKU is required")
    @Size(min = 2, max = 50, message = "SKU must be between 2 and 50 characters")
    private String sku;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.00", message = "Profit must be zero or positive")
    private BigDecimal profit;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String image;
}
