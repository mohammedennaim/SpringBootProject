package com.example.digitallogistics.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.digitallogistics.exception.ResourceNotFoundException;
import com.example.digitallogistics.exception.ValidationException;
import com.example.digitallogistics.model.dto.CarrierCreateDto;
import com.example.digitallogistics.model.dto.CarrierDto;
import com.example.digitallogistics.model.dto.CarrierStatusUpdateDto;
import com.example.digitallogistics.model.dto.CarrierUpdateDto;
import com.example.digitallogistics.model.entity.Carrier;
import com.example.digitallogistics.model.enums.CarrierStatus;
import com.example.digitallogistics.model.mapper.CarrierMapper;
import com.example.digitallogistics.repository.CarrierRepository;
import com.example.digitallogistics.service.CarrierService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CarrierServiceImpl implements CarrierService {

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CarrierDto> getAllCarriers(Pageable pageable) {
        @SuppressWarnings("null")
        Page<Carrier> carriers = carrierRepository.findAll(pageable);
        return carriers.map(carrierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierDto getCarrierById(UUID id) {
        @SuppressWarnings("null")
        Carrier carrier = carrierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + id));
        return carrierMapper.toDto(carrier);
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierDto getCarrierByCode(String code) {
        Carrier carrier = carrierRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with code: " + code));
        return carrierMapper.toDto(carrier);
    }

    @Override
    public CarrierDto createCarrier(CarrierCreateDto createDto) {
        if (carrierRepository.existsByCode(createDto.getCode())) {
            throw new ValidationException("Carrier with code already exists: " + createDto.getCode());
        }

        Carrier carrier = carrierMapper.toEntity(createDto);
        carrier.setStatus(CarrierStatus.ACTIVE);

        Carrier savedCarrier = carrierRepository.save(carrier);
        return carrierMapper.toDto(savedCarrier);
    }

    @Override
    public CarrierDto updateCarrier(UUID id, CarrierUpdateDto updateDto) {
        @SuppressWarnings("null")
        Carrier carrier = carrierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + id));

        carrierMapper.updateFromDto(updateDto, carrier);

        @SuppressWarnings("null")
        Carrier updatedCarrier = carrierRepository.save(carrier);
        return carrierMapper.toDto(updatedCarrier);
    }

    @Override
    public CarrierDto updateCarrierStatus(UUID id, CarrierStatusUpdateDto statusUpdate) {
        @SuppressWarnings("null")
        Carrier carrier = carrierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + id));

        validateStatusTransition(carrier.getStatus(), statusUpdate.getStatus());

        carrier.setStatus(statusUpdate.getStatus());

        Carrier updatedCarrier = carrierRepository.save(carrier);
        return carrierMapper.toDto(updatedCarrier);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarrierDto> getCarriersByStatus(CarrierStatus status, Pageable pageable) {
        Page<Carrier> carriers = carrierRepository.findByStatus(status, pageable);
        return carriers.map(carrierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarrierDto> getCarriersByName(String name, Pageable pageable) {
        Page<Carrier> carriers = carrierRepository.findByNameContainingIgnoreCase(name, pageable);
        return carriers.map(carrierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarrierDto> getCarriersByStatusAndName(
            CarrierStatus status, String name, Pageable pageable) {
        Page<Carrier> carriers = carrierRepository.findByStatusAndNameContaining(status, name, pageable);
        return carriers.map(carrierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarrierDto> getAllActiveCarriers() {
        List<Carrier> carriers = carrierRepository.findAllActive();
        return carriers.stream()
            .map(carrierMapper::toDto)
            .toList();
    }

    @SuppressWarnings("null")
    @Override
    public void deleteCarrier(UUID id) {
        Carrier carrier = carrierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + id));

        carrierRepository.delete(carrier);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return carrierRepository.existsByCode(code);
    }

    private void validateStatusTransition(CarrierStatus currentStatus, CarrierStatus newStatus) {
        if (currentStatus == newStatus) {
            return;
        }
    }
}