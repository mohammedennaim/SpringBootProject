package com.example.digitallogistics.model.dto;

import com.example.digitallogistics.model.enums.Role;

public class UserCreateDto {
    private String email;
    private String password;
    private Role role;
    // Make active nullable so we can use default=true when not provided by client
    private Boolean active = true;
    // New fields for Client creation
    private String name;
    private String contact;

    public UserCreateDto() {
    }


    public UserCreateDto(String email, String password, Role role, Boolean active, String name, String contact) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
        this.name = name;
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setRole(String role) {
        if (role == null) {
            this.role = null;
            return;
        }
        try {
            this.role = Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role value: " + role + ". Allowed values: ADMIN, CLIENT, WAREHOUSE_MANAGER");
        }
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
