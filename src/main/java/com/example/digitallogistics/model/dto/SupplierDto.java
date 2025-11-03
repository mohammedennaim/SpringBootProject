package com.example.digitallogistics.model.dto;

import java.util.UUID;

public class SupplierDto {
    private UUID id;
    private String name;
    private String contact;

    public SupplierDto() {
    }

    public SupplierDto(UUID id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}