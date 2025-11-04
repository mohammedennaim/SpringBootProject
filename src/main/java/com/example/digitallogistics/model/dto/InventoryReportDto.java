package com.example.digitallogistics.model.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO pour les statistiques d'inventaire
 */
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
    
    // Constructeurs
    public InventoryReportDto() {}

    // Classes internes pour les résumés
    public static class ProductStockSummary {
        private String productSku;
        private String productName;
        private Integer totalStock;
        private Integer availableStock;
        private BigDecimal unitPrice;
        private BigDecimal totalValue;
        private Double turnoverRate;
        
        public ProductStockSummary() {}
        
        public ProductStockSummary(String productSku, String productName, Integer totalStock, 
                                 Integer availableStock, BigDecimal unitPrice, BigDecimal totalValue, 
                                 Double turnoverRate) {
            this.productSku = productSku;
            this.productName = productName;
            this.totalStock = totalStock;
            this.availableStock = availableStock;
            this.unitPrice = unitPrice;
            this.totalValue = totalValue;
            this.turnoverRate = turnoverRate;
        }

        // Getters et Setters
        public String getProductSku() { return productSku; }
        public void setProductSku(String productSku) { this.productSku = productSku; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public Integer getTotalStock() { return totalStock; }
        public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }
        
        public Integer getAvailableStock() { return availableStock; }
        public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }
        
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        
        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
        
        public Double getTurnoverRate() { return turnoverRate; }
        public void setTurnoverRate(Double turnoverRate) { this.turnoverRate = turnoverRate; }
    }
    
    public static class WarehouseStockSummary {
        private String warehouseName;
        private Integer totalProducts;
        private Integer totalStock;
        private Integer availableStock;
        private BigDecimal totalValue;
        private Integer outOfStockCount;
        
        public WarehouseStockSummary() {}
        
        public WarehouseStockSummary(String warehouseName, Integer totalProducts, Integer totalStock,
                                   Integer availableStock, BigDecimal totalValue, Integer outOfStockCount) {
            this.warehouseName = warehouseName;
            this.totalProducts = totalProducts;
            this.totalStock = totalStock;
            this.availableStock = availableStock;
            this.totalValue = totalValue;
            this.outOfStockCount = outOfStockCount;
        }

        // Getters et Setters
        public String getWarehouseName() { return warehouseName; }
        public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
        
        public Integer getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }
        
        public Integer getTotalStock() { return totalStock; }
        public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }
        
        public Integer getAvailableStock() { return availableStock; }
        public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }
        
        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
        
        public Integer getOutOfStockCount() { return outOfStockCount; }
        public void setOutOfStockCount(Integer outOfStockCount) { this.outOfStockCount = outOfStockCount; }
    }

    // Getters et Setters principaux
    public Long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }
    
    public Long getActiveProducts() { return activeProducts; }
    public void setActiveProducts(Long activeProducts) { this.activeProducts = activeProducts; }
    
    public Long getInactiveProducts() { return inactiveProducts; }
    public void setInactiveProducts(Long inactiveProducts) { this.inactiveProducts = inactiveProducts; }
    
    public Long getOutOfStockProducts() { return outOfStockProducts; }
    public void setOutOfStockProducts(Long outOfStockProducts) { this.outOfStockProducts = outOfStockProducts; }
    
    public Long getLowStockProducts() { return lowStockProducts; }
    public void setLowStockProducts(Long lowStockProducts) { this.lowStockProducts = lowStockProducts; }
    
    public Long getOverstockedProducts() { return overstockedProducts; }
    public void setOverstockedProducts(Long overstockedProducts) { this.overstockedProducts = overstockedProducts; }
    
    public BigDecimal getTotalInventoryValue() { return totalInventoryValue; }
    public void setTotalInventoryValue(BigDecimal totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }
    
    public BigDecimal getLowStockValue() { return lowStockValue; }
    public void setLowStockValue(BigDecimal lowStockValue) { this.lowStockValue = lowStockValue; }
    
    public BigDecimal getOverstockValue() { return overstockValue; }
    public void setOverstockValue(BigDecimal overstockValue) { this.overstockValue = overstockValue; }
    
    public Double getStockTurnoverRate() { return stockTurnoverRate; }
    public void setStockTurnoverRate(Double stockTurnoverRate) { this.stockTurnoverRate = stockTurnoverRate; }
    
    public Double getStockoutRate() { return stockoutRate; }
    public void setStockoutRate(Double stockoutRate) { this.stockoutRate = stockoutRate; }
    
    public Double getFillRate() { return fillRate; }
    public void setFillRate(Double fillRate) { this.fillRate = fillRate; }
    
    public List<ProductStockSummary> getTopSellingProducts() { return topSellingProducts; }
    public void setTopSellingProducts(List<ProductStockSummary> topSellingProducts) { this.topSellingProducts = topSellingProducts; }
    
    public List<ProductStockSummary> getCriticalStockProducts() { return criticalStockProducts; }
    public void setCriticalStockProducts(List<ProductStockSummary> criticalStockProducts) { this.criticalStockProducts = criticalStockProducts; }
    
    public List<ProductStockSummary> getDeadStockProducts() { return deadStockProducts; }
    public void setDeadStockProducts(List<ProductStockSummary> deadStockProducts) { this.deadStockProducts = deadStockProducts; }
    
    public List<WarehouseStockSummary> getWarehouseStockSummaries() { return warehouseStockSummaries; }
    public void setWarehouseStockSummaries(List<WarehouseStockSummary> warehouseStockSummaries) { this.warehouseStockSummaries = warehouseStockSummaries; }
}