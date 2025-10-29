package com.example.digitallogistics.model.dto;

import com.example.digitallogistics.model.enums.Role;

public class UserCreateDto {
    private String email;
    private String password;
    private Role role;
    private boolean active;

    public UserCreateDto() {
    }

    public UserCreateDto(String email, String password, Role role, boolean active) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
