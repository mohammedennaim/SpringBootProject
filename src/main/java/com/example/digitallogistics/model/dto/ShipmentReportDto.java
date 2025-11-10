package com.example.digitallogistics.model.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les statistiques d'expéditions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
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
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DestinationSummary {
        private String destinationCity;
        private String destinationCountry;
        private Long totalShipments;
        private Double averageDeliveryTimeHours;
        private Double onTimeRate;
    }
}