package com.example.digitallogistics.model.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Warehouse manager user. Extends base User (JOINED inheritance).
 * Un manager peut gérer plusieurs entrepôts (One-to-Many).
 */
@Entity
@Table(name = "managers")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Manager extends User {

    // Un manager peut gérer plusieurs entrepôts
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Warehouse> warehouses = new ArrayList<>();

    /**
     * Ajouter un entrepôt à ce manager
     */
    public void addWarehouse(Warehouse warehouse) {
        warehouses.add(warehouse);
        warehouse.setManager(this);
    }

    /**
     * Retirer un entrepôt de ce manager
     */
    public void removeWarehouse(Warehouse warehouse) {
        warehouses.remove(warehouse);
        warehouse.setManager(null);
    }

}
