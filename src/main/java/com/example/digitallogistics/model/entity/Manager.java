package com.example.digitallogistics.model.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.Builder.Default;

@Entity
@Table(name = "managers")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Manager extends User {

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Default
    private List<Warehouse> warehouses = new ArrayList<>();

    public void addWarehouse(Warehouse warehouse) {
        warehouses.add(warehouse);
        warehouse.setManager(this);
    }

    public void removeWarehouse(Warehouse warehouse) {
        warehouses.remove(warehouse);
        warehouse.setManager(null);
    }

}
