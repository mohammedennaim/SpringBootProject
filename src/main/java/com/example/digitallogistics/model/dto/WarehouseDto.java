package com.example.digitallogistics.model.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseDto {
    private UUID id;
    private String code;
    private String name;
    private Boolean active;
    private UUID managerId;
    private String managerEmail;
}