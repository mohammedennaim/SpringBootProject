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
    public Carrier() {
    }

    public Carrier(Long id, String code, String name, String email, String phone, BigDecimal rate, Integer maxDailyShipments, CarrierStatus status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.rate = rate;
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

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getrate() {
        return rate;
    }

    public void setrate(BigDecimal rate) {
        this.rate = rate;
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
