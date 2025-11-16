package com.example.digitallogistics.model.entity;

import java.util.UUID;

import com.example.digitallogistics.model.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "active")
    private boolean active;

 
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }

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