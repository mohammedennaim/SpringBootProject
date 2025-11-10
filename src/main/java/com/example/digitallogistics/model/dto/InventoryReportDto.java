package com.example.digitallogistics.model.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les statistiques d'inventaire
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReportDto {

    // Métriques générales du stock
    private Long totalProducts;
    private Long activeProducts;
    private Long inactiveProducts;
    private Long outOfStockProducts;
    private Long lowStockProducts;
    private Long overstockedProducts;

    // Valeurs financières
    private BigDecimal totalInventoryValue;
    private BigDecimal lowStockValue;
    private BigDecimal overstockValue;

    // Métriques de performance
    private Double stockTurnoverRate; // Rotation du stock
    private Double stockoutRate; // Taux de rupture
    private Double fillRate; // Taux de service

    // Top produits
    private List<ProductStockSummary> topSellingProducts;
    private List<ProductStockSummary> criticalStockProducts;
    private List<ProductStockSummary> deadStockProducts;

    // Répartition par entrepôt
    private List<WarehouseStockSummary> warehouseStockSummaries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductStockSummary {
        private String productSku;
        private String productName;
        private Integer totalStock;
        private Integer availableStock;
        private BigDecimal unitPrice;
        private BigDecimal totalValue;
        private Double turnoverRate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WarehouseStockSummary {
        private String warehouseName;
        private Integer totalProducts;
        private Integer totalStock;
        private Integer availableStock;
        private BigDecimal totalValue;
        private Integer outOfStockCount;
    }
}