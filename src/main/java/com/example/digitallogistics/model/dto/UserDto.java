package com.example.digitallogistics.model.dto;

import java.util.UUID;
import com.example.digitallogistics.model.enums.Role;

public class UserDto {
    private UUID id;
    private String email;
    private Role role;
    private boolean active;

    public UserDto() {
    }

    public UserDto(UUID id, String email, Role role, boolean active) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
