package com.example.digitallogistics.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManagerDto {
    private UUID id;
    private String email;
    private List<UUID> warehouseIds = new ArrayList<>();  // Liste des IDs des entrepôts gérés
    private boolean active;

    public ManagerDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public List<UUID> getWarehouseIds() { return warehouseIds; }
    public void setWarehouseIds(List<UUID> warehouseIds) { this.warehouseIds = warehouseIds; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}