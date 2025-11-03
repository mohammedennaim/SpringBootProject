package com.example.digitallogistics.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.example.digitallogistics.model.dto.WarehouseCreateDto;
import com.example.digitallogistics.model.dto.WarehouseDto;
import com.example.digitallogistics.model.dto.WarehouseUpdateDto;
import com.example.digitallogistics.model.entity.Warehouse;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WarehouseMapper {
    WarehouseMapper INSTANCE = Mappers.getMapper(WarehouseMapper.class);

    WarehouseDto toDto(Warehouse warehouse);

    @Mapping(target = "id", ignore = true)
    Warehouse toEntity(WarehouseCreateDto dto);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(WarehouseUpdateDto dto, @MappingTarget Warehouse entity);
}