package com.example.digitallogistics.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.digitallogistics.model.enums.OrderStatus;

public class SalesOrderDto {
    private UUID id;
    private ClientDto client;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ClientDto getClient() { return client; }
    public void setClient(ClientDto client) { this.client = client; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getShippedAt() { return shippedAt; }
    public void setShippedAt(LocalDateTime shippedAt) { this.shippedAt = shippedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
}
