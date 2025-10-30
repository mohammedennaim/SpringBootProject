package com.example.digitallogistics.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.digitallogistics.model.enums.ShipmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {
    @Id
    private UUID id;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @Column(name = "planned_date")
    private LocalDateTime plannedDate;

    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
