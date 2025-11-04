package com.example.digitallogistics.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour les statistiques des commandes
 */
public class OrderReportDto {
    
    // Métriques générales
    private Long totalOrders;
    private Long pendingOrders;
    private Long processingOrders;
    private Long shippedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
    
    // Taux de performance
    private Double deliveryRate; // Pourcentage de commandes livrées
    private Double backorderRate; // Pourcentage de backorders
    private Double cancellationRate; // Pourcentage d'annulations
    
    // Métriques financières
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private BigDecimal pendingRevenue;
    
    // Métriques temporelles
    private Double averageProcessingTimeHours;
    private Double averageShippingTimeHours;
    
    // Période du rapport
    private LocalDate fromDate;
    private LocalDate toDate;
    
    // Constructeurs
    public OrderReportDto() {}
    
    public OrderReportDto(Long totalOrders, Long pendingOrders, Long processingOrders, 
                         Long shippedOrders, Long deliveredOrders, Long cancelledOrders,
                         Double deliveryRate, Double backorderRate, Double cancellationRate,
                         BigDecimal totalRevenue, BigDecimal averageOrderValue, BigDecimal pendingRevenue,
                         Double averageProcessingTimeHours, Double averageShippingTimeHours,
                         LocalDate fromDate, LocalDate toDate) {
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.processingOrders = processingOrders;
        this.shippedOrders = shippedOrders;
        this.deliveredOrders = deliveredOrders;
        this.cancelledOrders = cancelledOrders;
        this.deliveryRate = deliveryRate;
        this.backorderRate = backorderRate;
        this.cancellationRate = cancellationRate;
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = averageOrderValue;
        this.pendingRevenue = pendingRevenue;
        this.averageProcessingTimeHours = averageProcessingTimeHours;
        this.averageShippingTimeHours = averageShippingTimeHours;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    // Getters et Setters
    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(Long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public Long getProcessingOrders() {
        return processingOrders;
    }

    public void setProcessingOrders(Long processingOrders) {
        this.processingOrders = processingOrders;
    }

    public Long getShippedOrders() {
        return shippedOrders;
    }

    public void setShippedOrders(Long shippedOrders) {
        this.shippedOrders = shippedOrders;
    }

    public Long getDeliveredOrders() {
        return deliveredOrders;
    }

    public void setDeliveredOrders(Long deliveredOrders) {
        this.deliveredOrders = deliveredOrders;
    }

    public Long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(Long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public Double getDeliveryRate() {
        return deliveryRate;
    }

    public void setDeliveryRate(Double deliveryRate) {
        this.deliveryRate = deliveryRate;
    }

    public Double getBackorderRate() {
        return backorderRate;
    }

    public void setBackorderRate(Double backorderRate) {
        this.backorderRate = backorderRate;
    }

    public Double getCancellationRate() {
        return cancellationRate;
    }

    public void setCancellationRate(Double cancellationRate) {
        this.cancellationRate = cancellationRate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public BigDecimal getPendingRevenue() {
        return pendingRevenue;
    }

    public void setPendingRevenue(BigDecimal pendingRevenue) {
        this.pendingRevenue = pendingRevenue;
    }

    public Double getAverageProcessingTimeHours() {
        return averageProcessingTimeHours;
    }

    public void setAverageProcessingTimeHours(Double averageProcessingTimeHours) {
        this.averageProcessingTimeHours = averageProcessingTimeHours;
    }

    public Double getAverageShippingTimeHours() {
        return averageShippingTimeHours;
    }

    public void setAverageShippingTimeHours(Double averageShippingTimeHours) {
        this.averageShippingTimeHours = averageShippingTimeHours;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}