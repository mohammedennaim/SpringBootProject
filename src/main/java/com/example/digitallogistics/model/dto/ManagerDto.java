package com.example.digitallogistics.model.dto;

import java.util.UUID;

public class ManagerDto {
    private UUID id;
    private String email;
    private UUID warehouseId;
    private boolean active;

    public ManagerDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public UUID getWarehouseId() { return warehouseId; }
    public void setWarehouseId(UUID warehouseId) { this.warehouseId = warehouseId; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}