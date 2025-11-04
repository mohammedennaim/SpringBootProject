package com.example.digitallogistics.model.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarrierCreateDto {
    
    @NotBlank(message = "Code is required")
    @Size(min = 2, max = 20, message = "Code must be between 2 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Code must contain only uppercase letters, numbers and hyphens")
    private String code;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;
    
    @PositiveOrZero(message = "Rate must be positive or zero")
    private BigDecimal rate;
    
    @NotNull(message = "Max daily shipments is required")
    @Min(value = 1, message = "Max daily shipments must be at least 1")
    private Integer maxDailyShipments;
}