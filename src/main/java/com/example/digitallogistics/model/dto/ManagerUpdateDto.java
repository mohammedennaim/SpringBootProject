package com.example.digitallogistics.model.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerUpdateDto {
    @Email(message = "Format d'email invalide")
    private String email;

    private String password;
    private UUID warehouseId;
    private Boolean active;
}