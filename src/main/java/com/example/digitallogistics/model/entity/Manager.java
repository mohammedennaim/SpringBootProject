package com.example.digitallogistics.model.entity;

 

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Warehouse manager user. Extends base User (JOINED inheritance).
 */
@Entity
@Table(name = "managers")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Manager extends User {

    // Link to a warehouse this manager is responsible for (optional)
    @Column(name = "warehouse_id")
    private UUID warehouseId;

}
