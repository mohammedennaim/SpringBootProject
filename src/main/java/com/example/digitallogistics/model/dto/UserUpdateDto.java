package com.example.digitallogistics.model.dto;

import com.example.digitallogistics.model.enums.Role;

public class UserUpdateDto {
    private String email;
    private String password;
    private Role role;
    private Boolean active;

    public UserUpdateDto() {
    }

    public UserUpdateDto(String email, String password, Role role, Boolean active) {
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
