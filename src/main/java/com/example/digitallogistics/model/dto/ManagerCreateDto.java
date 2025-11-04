package com.example.digitallogistics.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ManagerCreateDto {
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
    
    // Liste des IDs des entrepôts que ce manager va gérer
    private List<UUID> warehouseIds = new ArrayList<>();
    private Boolean active = true;

    public ManagerCreateDto() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public List<UUID> getWarehouseIds() { return warehouseIds; }
    public void setWarehouseIds(List<UUID> warehouseIds) { this.warehouseIds = warehouseIds; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}