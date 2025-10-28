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

    @Column(name = "movement_type")
    @Enumerated(EnumType.STRING)
    private MovementType movementType;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;

    @Column(name = "reference_document")
    private String referenceDocument;

    @Column(name = "description")
    private String description;
    public InventoryMovement() {
    }

    public InventoryMovement(Long id, MovementType movementType, Integer quantity, LocalDateTime occurredAt, String referenceDocument, String description) {
        this.id = id;
        this.movementType = movementType;
        this.quantity = quantity;
        this.occurredAt = occurredAt;
        this.referenceDocument = referenceDocument;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
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

    public String getReferenceDocument() {
        return referenceDocument;
    }

    public void setReferenceDocument(String referenceDocument) {
        this.referenceDocument = referenceDocument;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
