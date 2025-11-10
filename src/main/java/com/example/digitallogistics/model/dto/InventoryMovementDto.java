package com.example.digitallogistics.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.digitallogistics.model.enums.MovementType;

public class InventoryMovementDto {
    private UUID id;
    private MovementType type;
    private Integer quantity;
    private LocalDateTime occurredAt;
    private String reference;
    private String description;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public MovementType getType() { return type; }
    public void setType(MovementType type) { this.type = type; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
