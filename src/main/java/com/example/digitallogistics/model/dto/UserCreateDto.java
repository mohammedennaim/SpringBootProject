package com.example.digitallogistics.model.dto;

import com.example.digitallogistics.model.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    // Make active nullable so we can use default=true when not provided by client
    @Default
    private Boolean active = true;

    // New fields for Client creation
    private String name;
    private String contact;

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
