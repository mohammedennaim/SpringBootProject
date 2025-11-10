package com.example.digitallogistics.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les statistiques des commandes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}