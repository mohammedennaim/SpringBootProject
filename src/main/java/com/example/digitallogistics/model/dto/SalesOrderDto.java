package com.example.digitallogistics.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.digitallogistics.model.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderDto {
    private UUID id;
    private ClientDto client;
    private OrderStatus status;
    private List<SalesOrderLineDto> lines;
    private LocalDateTime createdAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
}
