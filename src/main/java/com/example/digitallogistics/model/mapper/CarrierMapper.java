package com.example.digitallogistics.model.mapper;

import org.mapstruct.Mapper;

import com.example.digitallogistics.model.dto.CarrierDto;
import com.example.digitallogistics.model.entity.Carrier;

@Mapper(componentModel = "spring")
public interface CarrierMapper {

    CarrierDto toDto(Carrier carrier);

    Carrier toEntity(CarrierDto dto);
}