package com.example.digitallogistics.model.mapper;

import com.example.digitallogistics.model.dto.ClientDto;
import com.example.digitallogistics.model.dto.SalesOrderDto;
import com.example.digitallogistics.model.dto.SalesOrderLineDto;
import com.example.digitallogistics.model.entity.SalesOrder;
import com.example.digitallogistics.model.entity.SalesOrderLine;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class SalesOrderMapper {

    public static SalesOrderDto toDto(SalesOrder order, ClientMapper clientMapper, List<SalesOrderLine> lines) {
        if (order == null) return null;
        SalesOrderDto dto = new SalesOrderDto();
        dto.setId(order.getId());
        dto.setClient(clientMapper.toDto(order.getClient()));
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setShippedAt(order.getShippedAt());
        dto.setDeliveredAt(order.getDeliveredAt());

        if (lines != null) {
            List<SalesOrderLineDto> mapped = lines.stream().map(l -> {
                SalesOrderLineDto ld = new SalesOrderLineDto();
                if (l.getProduct() != null) {
                    ld.setProductId(l.getProduct().getId());
                    ld.setProductName(l.getProduct().getName());
                }
                ld.setQuantity(l.getQuantity());
                BigDecimal unitPrice = l.getUnitPrice() != null ? l.getUnitPrice() : BigDecimal.ZERO;
                ld.setUnitPrice(unitPrice);
                BigDecimal profit = BigDecimal.ONE;
                if (l.getProduct() != null && l.getProduct().getProfit() != null) {
                    profit = l.getProduct().getProfit();
                }
                ld.setProfit(profit);
                BigDecimal qty = new BigDecimal(ld.getQuantity() != null ? ld.getQuantity() : 0);
                BigDecimal total = unitPrice.multiply(qty).multiply(profit);
                ld.setTotalPrice(total);
                return ld;
            }).collect(Collectors.toList());
            dto.setLines(mapped);
        }

        return dto;
    }
}
