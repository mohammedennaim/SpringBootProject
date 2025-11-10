package com.example.digitallogistics.service;

import com.example.digitallogistics.model.dto.InventoryReportDto;
import com.example.digitallogistics.model.dto.OrderReportDto;
import com.example.digitallogistics.model.dto.ShipmentReportDto;
import java.time.LocalDate;
import java.util.UUID;

public interface ReportService {
    OrderReportDto getOrderReport(LocalDate fromDate, LocalDate toDate);
    InventoryReportDto getInventoryReport(UUID warehouseId);
    ShipmentReportDto getShipmentReport(LocalDate fromDate, LocalDate toDate, UUID carrierId);
}