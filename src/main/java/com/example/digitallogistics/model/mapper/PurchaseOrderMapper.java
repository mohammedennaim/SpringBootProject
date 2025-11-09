package com.example.digitallogistics.model.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.example.digitallogistics.model.dto.PurchaseOrderDto;
import com.example.digitallogistics.model.dto.PurchaseOrderLineDto;
import com.example.digitallogistics.model.entity.PurchaseOrder;
import com.example.digitallogistics.model.entity.PurchaseOrderLine;

public class PurchaseOrderMapper {

    public static PurchaseOrderDto toDto(PurchaseOrder po, SupplierMapper supplierMapper, List<PurchaseOrderLine> lines) {
        if (po == null) return null;
        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(po.getId());
        dto.setSupplier(supplierMapper.toDto(po.getSupplier()));
        dto.setStatus(po.getStatus());
        dto.setCreatedAt(po.getCreatedAt());
        dto.setExpectedDelivery(po.getExpectedDelivery());

        if (lines != null) {
            List<PurchaseOrderLineDto> mapped = lines.stream().map(l -> {
                PurchaseOrderLineDto ld = new PurchaseOrderLineDto();
                if (l.getProduct() != null) {
                    ld.setProductId(l.getProduct().getId());
                    ld.setProductName(l.getProduct().getName());
                }
                ld.setId(l.getId());
                ld.setQuantity(l.getQuantity());
                ld.setUnitPrice(l.getUnitPrice());
                return ld;
            }).collect(Collectors.toList());
            dto.setLines(mapped);
        }

        return dto;
    }
}
