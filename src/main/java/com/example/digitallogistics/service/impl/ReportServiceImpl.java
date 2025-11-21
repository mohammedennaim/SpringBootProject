package com.example.digitallogistics.service.impl;

import com.example.digitallogistics.model.dto.*;
import com.example.digitallogistics.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final String COUNTRY_MOROCCO = "Maroc";

    
    @Override
    public OrderReportDto getOrderReport(LocalDate fromDate, LocalDate toDate) {
        LocalDate startDate = fromDate != null ? fromDate : LocalDate.now().minusDays(30);
        LocalDate endDate = toDate != null ? toDate : LocalDate.now();
        
        try {
            OrderReportDto report = new OrderReportDto();

            report.setTotalOrders(150L);
            report.setPendingOrders(25L);
            report.setProcessingOrders(30L);
            report.setShippedOrders(45L);
            report.setDeliveredOrders(40L);
            report.setCancelledOrders(10L);

            double total = report.getTotalOrders().doubleValue();
            if (total > 0) {
                report.setDeliveryRate(report.getDeliveredOrders() / total * 100);
                report.setBackorderRate(report.getPendingOrders() / total * 100);
                report.setCancellationRate(report.getCancelledOrders() / total * 100);
            } else {
                report.setDeliveryRate(0.0);
                report.setBackorderRate(0.0);
                report.setCancellationRate(0.0);
            }
            report.setTotalRevenue(BigDecimal.valueOf(125000.00).setScale(2, RoundingMode.HALF_UP));
            report.setAverageOrderValue(report.getTotalRevenue().divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP));
            report.setPendingRevenue(BigDecimal.valueOf(18500.00).setScale(2, RoundingMode.HALF_UP));
            report.setAverageProcessingTimeHours(24.5);
            report.setAverageShippingTimeHours(48.2);
            report.setFromDate(startDate);
            report.setToDate(endDate);
            
            return report;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate order report", e);
        }
    }
    
    @Override
    public InventoryReportDto getInventoryReport(UUID warehouseId) {
        try {
            InventoryReportDto report = new InventoryReportDto();
            
            report.setTotalProducts(250L);
            report.setActiveProducts(230L);
            report.setInactiveProducts(20L);
            report.setOutOfStockProducts(15L);
            report.setLowStockProducts(35L);
            report.setOverstockedProducts(25L);
            report.setTotalInventoryValue(BigDecimal.valueOf(890000.00).setScale(2, RoundingMode.HALF_UP));
            report.setLowStockValue(BigDecimal.valueOf(45000.00).setScale(2, RoundingMode.HALF_UP));
            report.setOverstockValue(BigDecimal.valueOf(125000.00).setScale(2, RoundingMode.HALF_UP));
            report.setStockTurnoverRate(4.2);
            report.setStockoutRate(6.0);
            report.setFillRate(94.0);

            List<InventoryReportDto.ProductStockSummary> topSelling = Arrays.asList(
                new InventoryReportDto.ProductStockSummary("PROD-001", "Produit A", 150, 140, 
                    BigDecimal.valueOf(25.00), BigDecimal.valueOf(3750.00), 8.5),
                new InventoryReportDto.ProductStockSummary("PROD-002", "Produit B", 200, 180, 
                    BigDecimal.valueOf(45.00), BigDecimal.valueOf(9000.00), 6.2),
                new InventoryReportDto.ProductStockSummary("PROD-003", "Produit C", 75, 70, 
                    BigDecimal.valueOf(120.00), BigDecimal.valueOf(9000.00), 5.8)
            );
            report.setTopSellingProducts(topSelling);
            
            List<InventoryReportDto.ProductStockSummary> criticalStock = Arrays.asList(
                new InventoryReportDto.ProductStockSummary("PROD-010", "Produit J", 5, 5, 
                    BigDecimal.valueOf(85.00), BigDecimal.valueOf(425.00), 12.0),
                new InventoryReportDto.ProductStockSummary("PROD-015", "Produit O", 3, 2, 
                    BigDecimal.valueOf(150.00), BigDecimal.valueOf(450.00), 15.5)
            );
            report.setCriticalStockProducts(criticalStock);
            
            List<InventoryReportDto.ProductStockSummary> deadStock = List.of(
                    new InventoryReportDto.ProductStockSummary("PROD-050", "Produit obsolète", 100, 100,
                            BigDecimal.valueOf(10.00), BigDecimal.valueOf(1000.00), 0.1)
            );
            report.setDeadStockProducts(deadStock);
            
            List<InventoryReportDto.WarehouseStockSummary> warehouseSummaries = Arrays.asList(
                new InventoryReportDto.WarehouseStockSummary("Entrepôt Principal", 180, 15000, 13500, 
                    BigDecimal.valueOf(650000.00), 8),
                new InventoryReportDto.WarehouseStockSummary("Entrepôt Secondaire", 120, 8000, 7200, 
                    BigDecimal.valueOf(240000.00), 7)
            );
            report.setWarehouseStockSummaries(warehouseSummaries);
            
            return report;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate inventory report", e);
        }
    }
    
    @Override
    public ShipmentReportDto getShipmentReport(LocalDate fromDate, LocalDate toDate, UUID carrierId) {
        LocalDate startDate = fromDate != null ? fromDate : LocalDate.now().minusDays(30);
        LocalDate endDate = toDate != null ? toDate : LocalDate.now();
        
        try {
            ShipmentReportDto report = new ShipmentReportDto();
            report.setTotalShipments(120L);
            report.setPlannedShipments(15L);
            report.setInTransitShipments(25L);
            report.setDeliveredShipments(70L);
            report.setDelayedShipments(8L);
            report.setCancelledShipments(2L);
            
            double total = report.getTotalShipments().doubleValue();
            if (total > 0) {
                report.setOnTimeDeliveryRate((report.getDeliveredShipments() - report.getDelayedShipments()) / total * 100);
                report.setDelayRate(report.getDelayedShipments() / total * 100);
                report.setCancellationRate(report.getCancelledShipments() / total * 100);
            } else {
                report.setOnTimeDeliveryRate(0.0);
                report.setDelayRate(0.0);
                report.setCancellationRate(0.0);
            }
            report.setAverageShippingTimeHours(36.5);
            report.setAverageDelayHours(12.8);
            
            List<ShipmentReportDto.CarrierPerformance> carrierPerformances = Arrays.asList(
                new ShipmentReportDto.CarrierPerformance("DHL001", "DHL Express", 45L, 42L, 3L, 93.3, 24.5, 85, 100),
                new ShipmentReportDto.CarrierPerformance("UPS001", "UPS Standard", 35L, 30L, 4L, 85.7, 48.2, 70, 80),
                new ShipmentReportDto.CarrierPerformance("FDX001", "FedEx", 40L, 38L, 1L, 95.0, 36.0, 90, 120)
            );
            report.setCarrierPerformances(carrierPerformances);
            
            Map<String, Long> shipmentsPerPeriod = new LinkedHashMap<>();
            Map<String, Double> onTimeRatePerPeriod = new LinkedHashMap<>();
            for (int i = 7; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                shipmentsPerPeriod.put(dateStr, (long) (10 + Math.random() * 10));
                onTimeRatePerPeriod.put(dateStr, 85.0 + Math.random() * 15);
            }
            report.setShipmentsPerPeriod(shipmentsPerPeriod);
            report.setOnTimeRatePerPeriod(onTimeRatePerPeriod);
            
            List<ShipmentReportDto.DestinationSummary> topDestinations = Arrays.asList(
                new ShipmentReportDto.DestinationSummary("Casablanca", COUNTRY_MOROCCO, 35L, 24.5, 92.0),
                new ShipmentReportDto.DestinationSummary("Rabat", COUNTRY_MOROCCO, 25L, 36.2, 88.0),
                new ShipmentReportDto.DestinationSummary("Marrakech", COUNTRY_MOROCCO, 20L, 48.1, 85.0)
            );
            report.setTopDestinations(topDestinations);
            report.setFromDate(startDate);
            report.setToDate(endDate);
            
            return report; 
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate shipment report", e);
        }
    }
}