package com.example.digitallogistics.model.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO pour les statistiques d'expéditions
 */
public class ShipmentReportDto {
    
    // Métriques générales
    private Long totalShipments;
    private Long plannedShipments;
    private Long inTransitShipments;
    private Long deliveredShipments;
    private Long delayedShipments;
    private Long cancelledShipments;
    
    // Taux de performance
    private Double onTimeDeliveryRate; // Pourcentage livré à temps
    private Double delayRate; // Pourcentage en retard
    private Double cancellationRate; // Pourcentage annulé
    
    // Métriques temporelles
    private Double averageShippingTimeHours;
    private Double averageDelayHours;
    
    // Métriques par transporteur
    private List<CarrierPerformance> carrierPerformances;
    
    // Métriques par période (jour/semaine/mois)
    private Map<String, Long> shipmentsPerPeriod;
    private Map<String, Double> onTimeRatePerPeriod;
    
    // Période du rapport
    private LocalDate fromDate;
    private LocalDate toDate;
    
    // Top destinations
    private List<DestinationSummary> topDestinations;
    
    // Constructeurs
    public ShipmentReportDto() {}
    
    // Classes internes
    public static class CarrierPerformance {
        private String carrierCode;
        private String carrierName;
        private Long totalShipments;
        private Long deliveredShipments;
        private Long delayedShipments;
        private Double onTimeRate;
        private Double averageDeliveryTimeHours;
        private Integer currentCapacityUsed;
        private Integer maxDailyCapacity;
        
        public CarrierPerformance() {}
        
        public CarrierPerformance(String carrierCode, String carrierName, Long totalShipments,
                                Long deliveredShipments, Long delayedShipments, Double onTimeRate,
                                Double averageDeliveryTimeHours, Integer currentCapacityUsed,
                                Integer maxDailyCapacity) {
            this.carrierCode = carrierCode;
            this.carrierName = carrierName;
            this.totalShipments = totalShipments;
            this.deliveredShipments = deliveredShipments;
            this.delayedShipments = delayedShipments;
            this.onTimeRate = onTimeRate;
            this.averageDeliveryTimeHours = averageDeliveryTimeHours;
            this.currentCapacityUsed = currentCapacityUsed;
            this.maxDailyCapacity = maxDailyCapacity;
        }

        // Getters et Setters
        public String getCarrierCode() { return carrierCode; }
        public void setCarrierCode(String carrierCode) { this.carrierCode = carrierCode; }
        
        public String getCarrierName() { return carrierName; }
        public void setCarrierName(String carrierName) { this.carrierName = carrierName; }
        
        public Long getTotalShipments() { return totalShipments; }
        public void setTotalShipments(Long totalShipments) { this.totalShipments = totalShipments; }
        
        public Long getDeliveredShipments() { return deliveredShipments; }
        public void setDeliveredShipments(Long deliveredShipments) { this.deliveredShipments = deliveredShipments; }
        
        public Long getDelayedShipments() { return delayedShipments; }
        public void setDelayedShipments(Long delayedShipments) { this.delayedShipments = delayedShipments; }
        
        public Double getOnTimeRate() { return onTimeRate; }
        public void setOnTimeRate(Double onTimeRate) { this.onTimeRate = onTimeRate; }
        
        public Double getAverageDeliveryTimeHours() { return averageDeliveryTimeHours; }
        public void setAverageDeliveryTimeHours(Double averageDeliveryTimeHours) { this.averageDeliveryTimeHours = averageDeliveryTimeHours; }
        
        public Integer getCurrentCapacityUsed() { return currentCapacityUsed; }
        public void setCurrentCapacityUsed(Integer currentCapacityUsed) { this.currentCapacityUsed = currentCapacityUsed; }
        
        public Integer getMaxDailyCapacity() { return maxDailyCapacity; }
        public void setMaxDailyCapacity(Integer maxDailyCapacity) { this.maxDailyCapacity = maxDailyCapacity; }
    }
    
    public static class DestinationSummary {
        private String destinationCity;
        private String destinationCountry;
        private Long totalShipments;
        private Double averageDeliveryTimeHours;
        private Double onTimeRate;
        
        public DestinationSummary() {}
        
        public DestinationSummary(String destinationCity, String destinationCountry, Long totalShipments,
                                Double averageDeliveryTimeHours, Double onTimeRate) {
            this.destinationCity = destinationCity;
            this.destinationCountry = destinationCountry;
            this.totalShipments = totalShipments;
            this.averageDeliveryTimeHours = averageDeliveryTimeHours;
            this.onTimeRate = onTimeRate;
        }

        // Getters et Setters
        public String getDestinationCity() { return destinationCity; }
        public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
        
        public String getDestinationCountry() { return destinationCountry; }
        public void setDestinationCountry(String destinationCountry) { this.destinationCountry = destinationCountry; }
        
        public Long getTotalShipments() { return totalShipments; }
        public void setTotalShipments(Long totalShipments) { this.totalShipments = totalShipments; }
        
        public Double getAverageDeliveryTimeHours() { return averageDeliveryTimeHours; }
        public void setAverageDeliveryTimeHours(Double averageDeliveryTimeHours) { this.averageDeliveryTimeHours = averageDeliveryTimeHours; }
        
        public Double getOnTimeRate() { return onTimeRate; }
        public void setOnTimeRate(Double onTimeRate) { this.onTimeRate = onTimeRate; }
    }

    // Getters et Setters principaux
    public Long getTotalShipments() { return totalShipments; }
    public void setTotalShipments(Long totalShipments) { this.totalShipments = totalShipments; }
    
    public Long getPlannedShipments() { return plannedShipments; }
    public void setPlannedShipments(Long plannedShipments) { this.plannedShipments = plannedShipments; }
    
    public Long getInTransitShipments() { return inTransitShipments; }
    public void setInTransitShipments(Long inTransitShipments) { this.inTransitShipments = inTransitShipments; }
    
    public Long getDeliveredShipments() { return deliveredShipments; }
    public void setDeliveredShipments(Long deliveredShipments) { this.deliveredShipments = deliveredShipments; }
    
    public Long getDelayedShipments() { return delayedShipments; }
    public void setDelayedShipments(Long delayedShipments) { this.delayedShipments = delayedShipments; }
    
    public Long getCancelledShipments() { return cancelledShipments; }
    public void setCancelledShipments(Long cancelledShipments) { this.cancelledShipments = cancelledShipments; }
    
    public Double getOnTimeDeliveryRate() { return onTimeDeliveryRate; }
    public void setOnTimeDeliveryRate(Double onTimeDeliveryRate) { this.onTimeDeliveryRate = onTimeDeliveryRate; }
    
    public Double getDelayRate() { return delayRate; }
    public void setDelayRate(Double delayRate) { this.delayRate = delayRate; }
    
    public Double getCancellationRate() { return cancellationRate; }
    public void setCancellationRate(Double cancellationRate) { this.cancellationRate = cancellationRate; }
    
    public Double getAverageShippingTimeHours() { return averageShippingTimeHours; }
    public void setAverageShippingTimeHours(Double averageShippingTimeHours) { this.averageShippingTimeHours = averageShippingTimeHours; }
    
    public Double getAverageDelayHours() { return averageDelayHours; }
    public void setAverageDelayHours(Double averageDelayHours) { this.averageDelayHours = averageDelayHours; }
    
    public List<CarrierPerformance> getCarrierPerformances() { return carrierPerformances; }
    public void setCarrierPerformances(List<CarrierPerformance> carrierPerformances) { this.carrierPerformances = carrierPerformances; }
    
    public Map<String, Long> getShipmentsPerPeriod() { return shipmentsPerPeriod; }
    public void setShipmentsPerPeriod(Map<String, Long> shipmentsPerPeriod) { this.shipmentsPerPeriod = shipmentsPerPeriod; }
    
    public Map<String, Double> getOnTimeRatePerPeriod() { return onTimeRatePerPeriod; }
    public void setOnTimeRatePerPeriod(Map<String, Double> onTimeRatePerPeriod) { this.onTimeRatePerPeriod = onTimeRatePerPeriod; }
    
    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
    
    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }
    
    public List<DestinationSummary> getTopDestinations() { return topDestinations; }
    public void setTopDestinations(List<DestinationSummary> topDestinations) { this.topDestinations = topDestinations; }
}