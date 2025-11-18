package com.example.digitallogistics.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "shipment_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentSlot {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(name = "slot_date")
    private LocalDate slotDate;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "current_usage")
    private Integer currentUsage;

    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.currentUsage == null) {
            this.currentUsage = 0;
        }
    }
}