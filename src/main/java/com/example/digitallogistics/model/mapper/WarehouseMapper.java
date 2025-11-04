package com.example.digitallogistics.model.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import com.example.digitallogistics.model.dto.WarehouseCreateDto;
import com.example.digitallogistics.model.dto.WarehouseDto;
import com.example.digitallogistics.model.dto.WarehouseUpdateDto;
import com.example.digitallogistics.model.entity.Warehouse;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WarehouseMapper {
    WarehouseMapper INSTANCE = Mappers.getMapper(WarehouseMapper.class);

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerEmail", source = "manager.email")
    WarehouseDto toDto(Warehouse warehouse);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", ignore = true)
    Warehouse toEntity(WarehouseCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", ignore = true)
    void updateFromDto(WarehouseUpdateDto dto, @MappingTarget Warehouse entity);
}