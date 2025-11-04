package com.example.digitallogistics.model.dto;

import java.util.UUID;
import jakarta.validation.constraints.Email;

public class ManagerUpdateDto {
    @Email(message = "Format d'email invalide")
    private String email;
    
    private String password;
    private UUID warehouseId;
    private Boolean active;

    public ManagerUpdateDto() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public UUID getWarehouseId() { return warehouseId; }
    public void setWarehouseId(UUID warehouseId) { this.warehouseId = warehouseId; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}