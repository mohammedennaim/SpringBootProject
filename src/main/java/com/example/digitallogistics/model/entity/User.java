package com.example.digitallogistics.model.entity;

import java.util.UUID;

import com.example.digitallogistics.model.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "active")
    private boolean active;

    // Keep id/role initialization in a single PrePersist callback to avoid
    // the JPA limitation that only one callback method may be annotated per
    // lifecycle event on the same bean class in this environment.
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }

        // Ensure role column is consistent with subclass type when missing.
        if (this.role == null) {
            if (this instanceof Admin) {
                this.role = Role.ADMIN;
            } else if (this instanceof Manager) {
                this.role = Role.WAREHOUSE_MANAGER;
            } else if (this instanceof Client) {
                this.role = Role.CLIENT;
            }
        }
    }
    
}