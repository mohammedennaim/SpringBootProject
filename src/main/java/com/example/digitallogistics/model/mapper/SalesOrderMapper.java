package com.example.digitallogistics.model.mapper;

import com.example.digitallogistics.model.dto.ClientDto;
import com.example.digitallogistics.model.dto.SalesOrderDto;
import com.example.digitallogistics.model.entity.SalesOrder;

public class SalesOrderMapper {

    public static SalesOrderDto toDto(SalesOrder order, ClientMapper clientMapper) {
        if (order == null) return null;
        SalesOrderDto dto = new SalesOrderDto();
        dto.setId(order.getId());
        dto.setClient(clientMapper.toDto(order.getClient()));
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setShippedAt(order.getShippedAt());
        dto.setDeliveredAt(order.getDeliveredAt());
        return dto;
    }
}
