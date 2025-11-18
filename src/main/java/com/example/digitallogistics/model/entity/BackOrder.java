package com.example.digitallogistics.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "back_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackOrder {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "original_order_id")
    private SalesOrder originalOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(name = "quantity_needed")
    private Integer quantityNeeded;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    @Column(name = "is_fulfilled")
    private Boolean isFulfilled;

    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isFulfilled == null) {
            this.isFulfilled = false;
        }
    }
}