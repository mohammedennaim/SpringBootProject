package com.example.digitallogistics.service.impl;

import com.example.digitallogistics.model.dto.InventoryReportDto;
import com.example.digitallogistics.model.dto.OrderReportDto;
import com.example.digitallogistics.model.dto.ShipmentReportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceImplTest {

    private ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportServiceImpl();
    }

    @Test
    void getOrderReport_WithValidDates_ShouldReturnReport() {
        LocalDate fromDate = LocalDate.now().minusDays(30);
        LocalDate toDate = LocalDate.now();

        OrderReportDto report = reportService.getOrderReport(fromDate, toDate);

        assertNotNull(report);
        assertEquals(150L, report.getTotalOrders());
        assertEquals(25L, report.getPendingOrders());
        assertEquals(30L, report.getProcessingOrders());
        assertEquals(45L, report.getShippedOrders());
        assertEquals(40L, report.getDeliveredOrders());
        assertEquals(10L, report.getCancelledOrders());
        assertEquals(0, new BigDecimal("125000.00").compareTo(report.getTotalRevenue()));
        assertEquals(fromDate, report.getFromDate());
        assertEquals(toDate, report.getToDate());
        assertTrue(report.getDeliveryRate() > 0);
        assertTrue(report.getBackorderRate() > 0);
        assertTrue(report.getCancellationRate() > 0);
        assertNotNull(report.getAverageOrderValue());
        assertTrue(report.getAverageProcessingTimeHours() > 0);
        assertTrue(report.getAverageShippingTimeHours() > 0);
    }

    @Test
    void getOrderReport_WithNullDates_ShouldUseDefaults() {
        OrderReportDto report = reportService.getOrderReport(null, null);

        assertNotNull(report);
        assertEquals(150L, report.getTotalOrders());
        assertNotNull(report.getFromDate());
        assertNotNull(report.getToDate());
        assertEquals(LocalDate.now().minusDays(30), report.getFromDate());
        assertEquals(LocalDate.now(), report.getToDate());
    }

    @Test
    void getInventoryReport_WithWarehouseId_ShouldReturnReport() {
        UUID warehouseId = UUID.randomUUID();

        InventoryReportDto report = reportService.getInventoryReport(warehouseId);

        assertNotNull(report);
        assertEquals(250L, report.getTotalProducts());
        assertEquals(230L, report.getActiveProducts());
        assertEquals(20L, report.getInactiveProducts());
        assertEquals(15L, report.getOutOfStockProducts());
        assertEquals(35L, report.getLowStockProducts());
        assertEquals(25L, report.getOverstockedProducts());
        assertEquals(0, new BigDecimal("890000.00").compareTo(report.getTotalInventoryValue()));
        assertEquals(0, new BigDecimal("45000.00").compareTo(report.getLowStockValue()));
        assertEquals(0, new BigDecimal("125000.00").compareTo(report.getOverstockValue()));
        assertEquals(4.2, report.getStockTurnoverRate());
        assertEquals(6.0, report.getStockoutRate());
        assertEquals(94.0, report.getFillRate());
        
        assertNotNull(report.getTopSellingProducts());
        assertEquals(3, report.getTopSellingProducts().size());
        
        assertNotNull(report.getCriticalStockProducts());
        assertEquals(2, report.getCriticalStockProducts().size());
        
        assertNotNull(report.getDeadStockProducts());
        assertEquals(1, report.getDeadStockProducts().size());
        
        assertNotNull(report.getWarehouseStockSummaries());
        assertEquals(2, report.getWarehouseStockSummaries().size());
    }

    @Test
    void getInventoryReport_WithNullWarehouseId_ShouldReturnReport() {
        InventoryReportDto report = reportService.getInventoryReport(null);

        assertNotNull(report);
        assertEquals(250L, report.getTotalProducts());
    }

    @Test
    void getShipmentReport_WithValidParameters_ShouldReturnReport() {
        LocalDate fromDate = LocalDate.now().minusDays(30);
        LocalDate toDate = LocalDate.now();
        UUID carrierId = UUID.randomUUID();

        ShipmentReportDto report = reportService.getShipmentReport(fromDate, toDate, carrierId);

        assertNotNull(report);
        assertEquals(120L, report.getTotalShipments());
        assertEquals(15L, report.getPlannedShipments());
        assertEquals(25L, report.getInTransitShipments());
        assertEquals(70L, report.getDeliveredShipments());
        assertEquals(8L, report.getDelayedShipments());
        assertEquals(2L, report.getCancelledShipments());
        assertEquals(fromDate, report.getFromDate());
        assertEquals(toDate, report.getToDate());
        assertTrue(report.getOnTimeDeliveryRate() > 0);
        assertTrue(report.getDelayRate() > 0);
        assertTrue(report.getCancellationRate() > 0);
        assertTrue(report.getAverageShippingTimeHours() > 0);
        assertTrue(report.getAverageDelayHours() > 0);
        
        assertNotNull(report.getCarrierPerformances());
        assertEquals(3, report.getCarrierPerformances().size());
        
        assertNotNull(report.getShipmentsPerPeriod());
        assertFalse(report.getShipmentsPerPeriod().isEmpty());
        
        assertNotNull(report.getOnTimeRatePerPeriod());
        assertFalse(report.getOnTimeRatePerPeriod().isEmpty());
        
        assertNotNull(report.getTopDestinations());
        assertEquals(3, report.getTopDestinations().size());
    }

    @Test
    void getShipmentReport_WithNullDates_ShouldUseDefaults() {
        ShipmentReportDto report = reportService.getShipmentReport(null, null, null);

        assertNotNull(report);
        assertEquals(120L, report.getTotalShipments());
        assertNotNull(report.getFromDate());
        assertNotNull(report.getToDate());
        assertEquals(LocalDate.now().minusDays(30), report.getFromDate());
        assertEquals(LocalDate.now(), report.getToDate());
    }
}