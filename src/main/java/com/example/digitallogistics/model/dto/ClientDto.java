package com.example.digitallogistics.model.dto;

import java.util.UUID;

public class ClientDto {
    private UUID id;
    private String name;
    private String contact;
    private Boolean active;

    public ClientDto() {}

    public ClientDto(UUID id, String name, String contact, Boolean active) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.active = active;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
