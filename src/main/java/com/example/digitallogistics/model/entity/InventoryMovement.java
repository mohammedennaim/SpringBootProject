package com.example.digitallogistics.model.entity;

import java.time.LocalDateTime;

import com.example.digitallogistics.model.enums.MovementType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MovementType type;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;

    @Column(name = "reference_document")
    private String reference;

    @Column(name = "description")
    private String description;
    public InventoryMovement() {
    }

    public InventoryMovement(Long id, MovementType type, Integer quantity, LocalDateTime occurredAt, String reference, String description) {
        this.id = id;
        this.type = type;
        this.quantity = quantity;
        this.occurredAt = occurredAt;
        this.reference = reference;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MovementType gettype() {
        return type;
    }

    public void settype(MovementType type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
