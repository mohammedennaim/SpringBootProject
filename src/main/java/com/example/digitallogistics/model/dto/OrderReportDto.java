package com.example.digitallogistics.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderReportDto {
    private Long totalOrders;
    private Long pendingOrders;
    private Long processingOrders;
    private Long shippedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
    private Double deliveryRate;
    private Double backorderRate;
    private Double cancellationRate;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private BigDecimal pendingRevenue;
    private Double averageProcessingTimeHours;
    private Double averageShippingTimeHours;
    private LocalDate fromDate;
    private LocalDate toDate;
}