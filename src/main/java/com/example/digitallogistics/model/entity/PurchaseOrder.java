package com.example.digitallogistics.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.digitallogistics.model.enums.PurchaseOrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expected_delivery")
    private LocalDateTime expectedDelivery;

    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
