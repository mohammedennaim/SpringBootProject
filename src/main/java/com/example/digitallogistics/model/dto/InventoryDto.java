package com.example.digitallogistics.model.dto;

import java.util.UUID;

public class InventoryDto {
    private UUID id;
    private UUID warehouseId;
    private String warehouseName;
    private UUID productId;
    private String productSku;
    private String productName;
    private Integer qtyOnHand;
    private Integer qtyReserved;

    public InventoryDto() {
    }

    public InventoryDto(UUID id, UUID warehouseId, String warehouseName, UUID productId, 
                       String productSku, String productName, Integer qtyOnHand, Integer qtyReserved) {
        this.id = id;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
    this.qtyOnHand = qtyOnHand;
    this.qtyReserved = qtyReserved;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(Integer qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public Integer getQtyReserved() {
        return qtyReserved;
    }

    public void setQtyReserved(Integer qtyReserved) {
        this.qtyReserved = qtyReserved;
    }

}