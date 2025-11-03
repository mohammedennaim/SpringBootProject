package com.example.digitallogistics.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.example.digitallogistics.model.dto.SupplierCreateDto;
import com.example.digitallogistics.model.dto.SupplierDto;
import com.example.digitallogistics.model.dto.SupplierUpdateDto;
import com.example.digitallogistics.model.entity.Supplier;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SupplierMapper {
    SupplierMapper INSTANCE = Mappers.getMapper(SupplierMapper.class);

    SupplierDto toDto(Supplier supplier);

    @Mapping(target = "id", ignore = true)
    Supplier toEntity(SupplierCreateDto dto);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(SupplierUpdateDto dto, @MappingTarget Supplier entity);
}