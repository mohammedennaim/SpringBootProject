package com.example.digitallogistics.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.digitallogistics.model.dto.InventoryReportDto;
import com.example.digitallogistics.model.dto.OrderReportDto;
import com.example.digitallogistics.model.dto.ShipmentReportDto;
import com.example.digitallogistics.service.ReportService;
import com.example.digitallogistics.util.JwtTokenProvider;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void getOrderReport_shouldReturnReport() throws Exception {
        OrderReportDto report = new OrderReportDto();
        report.setTotalOrders(100L);
        report.setDeliveredOrders(80L);

        when(reportService.getOrderReport(any(), any())).thenReturn(report);

        mockMvc.perform(get("/api/reports/orders")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(100));
    }

    @Test
    void getInventoryReport_shouldReturnReport() throws Exception {
        InventoryReportDto report = new InventoryReportDto();
        report.setTotalProducts(50L);

        when(reportService.getInventoryReport(any())).thenReturn(report);

        mockMvc.perform(get("/api/reports/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(50));
    }

    @Test
    void getInventoryReport_withWarehouseId_shouldReturnReport() throws Exception {
        UUID warehouseId = UUID.randomUUID();
        InventoryReportDto report = new InventoryReportDto();
        report.setTotalProducts(30L);

        when(reportService.getInventoryReport(any())).thenReturn(report);

        mockMvc.perform(get("/api/reports/inventory")
                .param("warehouseId", warehouseId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(30));
    }

    @Test
    void getShipmentReport_shouldReturnReport() throws Exception {
        ShipmentReportDto report = new ShipmentReportDto();
        report.setTotalShipments(200L);
        report.setDeliveredShipments(180L);

        when(reportService.getShipmentReport(any(), any(), any())).thenReturn(report);

        mockMvc.perform(get("/api/reports/shipments")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalShipments").value(200));
    }

    @Test
    void getShipmentReport_withCarrierId_shouldReturnReport() throws Exception {
        UUID carrierId = UUID.randomUUID();
        ShipmentReportDto report = new ShipmentReportDto();
        report.setTotalShipments(50L);

        when(reportService.getShipmentReport(any(), any(), any())).thenReturn(report);

        mockMvc.perform(get("/api/reports/shipments")
                .param("carrierId", carrierId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalShipments").value(50));
    }
}
