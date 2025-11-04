package com.example.digitallogistics.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.digitallogistics.model.dto.CarrierCreateDto;
import com.example.digitallogistics.model.dto.CarrierDto;
import com.example.digitallogistics.model.dto.CarrierUpdateDto;
import com.example.digitallogistics.model.entity.Carrier;

@Mapper(componentModel = "spring")
public interface CarrierMapper {

    CarrierDto toDto(Carrier carrier);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Carrier toEntity(CarrierCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateFromDto(CarrierUpdateDto dto, @MappingTarget Carrier carrier);
}