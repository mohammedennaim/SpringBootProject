package com.example.digitallogistics.model.entity;

import java.math.BigDecimal;
import com.example.digitallogistics.model.enums.CarrierStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "carriers")
public class Carrier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "shipping_rate")
    private BigDecimal shippingRate;

    @Column(name = "max_daily_shipments")
    private Integer maxDailyShipments;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CarrierStatus status;
    public Carrier() {
    }

    public Carrier(Long id, String code, String name, String contactEmail, String contactPhone, BigDecimal shippingRate, Integer maxDailyShipments, CarrierStatus status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.shippingRate = shippingRate;
        this.maxDailyShipments = maxDailyShipments;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public BigDecimal getShippingRate() {
        return shippingRate;
    }

    public void setShippingRate(BigDecimal shippingRate) {
        this.shippingRate = shippingRate;
    }

    public Integer getMaxDailyShipments() {
        return maxDailyShipments;
    }

    public void setMaxDailyShipments(Integer maxDailyShipments) {
        this.maxDailyShipments = maxDailyShipments;
    }

    public CarrierStatus getStatus() {
        return status;
    }

    public void setStatus(CarrierStatus status) {
        this.status = status;
    }
}
