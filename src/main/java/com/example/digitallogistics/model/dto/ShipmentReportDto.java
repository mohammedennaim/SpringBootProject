package com.example.digitallogistics.model.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentReportDto {
    private Long totalShipments;
    private Long plannedShipments;
    private Long inTransitShipments;
    private Long deliveredShipments;
    private Long delayedShipments;
    private Long cancelledShipments;
    private Double onTimeDeliveryRate;
    private Double delayRate;
    private Double cancellationRate;
    private Double averageShippingTimeHours;
    private Double averageDelayHours;
    private List<CarrierPerformance> carrierPerformances;
    private Map<String, Long> shipmentsPerPeriod;
    private Map<String, Double> onTimeRatePerPeriod;
    private LocalDate fromDate;
    private LocalDate toDate;
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