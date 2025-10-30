package com.example.digitallogistics.model.dto;

public class ClientCreateDto {
    private String name;
    private String contact;
    private Boolean active;

    public ClientCreateDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
