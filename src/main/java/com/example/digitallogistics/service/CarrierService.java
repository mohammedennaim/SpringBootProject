package com.example.digitallogistics.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.digitallogistics.model.dto.CarrierCreateDto;
import com.example.digitallogistics.model.dto.CarrierDto;
import com.example.digitallogistics.model.dto.CarrierStatusUpdateDto;
import com.example.digitallogistics.model.dto.CarrierUpdateDto;
import com.example.digitallogistics.model.enums.CarrierStatus;

public interface CarrierService {
    Page<CarrierDto> getAllCarriers(Pageable pageable);

    CarrierDto getCarrierById(UUID id);

    CarrierDto getCarrierByCode(String code);

    CarrierDto createCarrier(CarrierCreateDto createDto);

    CarrierDto updateCarrier(UUID id, CarrierUpdateDto updateDto);

    CarrierDto updateCarrierStatus(UUID id, CarrierStatusUpdateDto statusUpdate);

    Page<CarrierDto> getCarriersByStatus(CarrierStatus status, Pageable pageable);

    Page<CarrierDto> getCarriersByName(String name, Pageable pageable);

    Page<CarrierDto> getCarriersByStatusAndName(
        CarrierStatus status, String name, Pageable pageable);

    List<CarrierDto> getAllActiveCarriers();

    void deleteCarrier(UUID id);

    boolean existsByCode(String code);
}