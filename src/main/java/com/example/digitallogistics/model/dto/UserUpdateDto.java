package com.example.digitallogistics.model.dto;

import com.example.digitallogistics.model.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {
    private String email;
    private String password;
    private Role role;
    private Boolean active;

    // Accept role as a case-insensitive String in JSON bodies (e.g. "client" -> Role.CLIENT)
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
}
