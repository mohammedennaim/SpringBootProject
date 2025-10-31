package com.example.digitallogistics.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin user. Extends base User (JOINED inheritance).
 */
@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "id")
@jakarta.persistence.DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends User {

    @Column(name = "notes")
    private String notes;

}
