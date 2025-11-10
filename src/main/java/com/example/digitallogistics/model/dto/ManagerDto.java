package com.example.digitallogistics.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerDto {
    private UUID id;
    private String email;

    @Default
    private List<UUID> warehouseIds = new ArrayList<>();  // Liste des IDs des entrepôts gérés

    private boolean active;
}