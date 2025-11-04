package com.example.digitallogistics.model.dto;

import java.util.UUID;

public class WarehouseDto {
    private UUID id;
    private String code;
    private String name;
    private Boolean active;
    private UUID managerId;  // ID du manager qui gère cet entrepôt
    private String managerEmail;  // Email du manager (optionnel, pour affichage)

    public WarehouseDto() {
    }

    public WarehouseDto(UUID id, String code, String name, Boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }
}