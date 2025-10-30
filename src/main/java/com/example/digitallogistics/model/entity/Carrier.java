package com.example.digitallogistics.model.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.digitallogistics.model.enums.CarrierStatus;

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
@Table(name = "carriers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrier {
    @Id
    private UUID id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "shipping_rate")
    private BigDecimal rate;

    @Column(name = "max_daily_shipments")
    private Integer maxDailyShipments;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CarrierStatus status;

    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
