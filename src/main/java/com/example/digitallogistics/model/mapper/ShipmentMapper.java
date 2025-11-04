package com.example.digitallogistics.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.digitallogistics.model.dto.ShipmentCreateDto;
import com.example.digitallogistics.model.dto.ShipmentDto;
import com.example.digitallogistics.model.entity.Shipment;

@Mapper(componentModel = "spring", uses = {WarehouseMapper.class, CarrierMapper.class})
public interface ShipmentMapper {

    ShipmentDto toDto(Shipment shipment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "carrier", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "shippedAt", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    Shipment toEntity(ShipmentCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "carrier", ignore = true)
    @Mapping(target = "trackingNumber", ignore = true)
    @Mapping(target = "shippedAt", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    void updateEntityFromDto(ShipmentDto dto, @MappingTarget Shipment shipment);
}