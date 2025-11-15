package com.example.digitallogistics.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CarrierServiceImplTest {

    @Mock
    private CarrierRepository carrierRepository;

    @Mock
    private CarrierMapper carrierMapper;

    @InjectMocks
    private CarrierServiceImpl carrierService;

    private UUID carrierId;
    private Carrier carrier;
    private CarrierDto carrierDto;

    @BeforeEach
    void setUp() {
        carrierId = UUID.randomUUID();
        
        carrier = new Carrier();
        carrier.setId(carrierId);
        carrier.setCode("CAR001");
        carrier.setName("Test Carrier");
        carrier.setStatus(CarrierStatus.ACTIVE);
        carrier.setEmail("contact@carrier.com");
        carrier.setPhone("1234567890");
        
        carrierDto = new CarrierDto();
        carrierDto.setId(carrierId);
        carrierDto.setCode("CAR001");
        carrierDto.setName("Test Carrier");
        carrierDto.setStatus(CarrierStatus.ACTIVE);
    }

    @Test
    void getAllCarriers_shouldReturnPagedCarriers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Carrier> carrierPage = new PageImpl<>(List.of(carrier));
        
        when(carrierRepository.findAll(pageable)).thenReturn(carrierPage);
        when(carrierMapper.toDto(carrier)).thenReturn(carrierDto);

        Page<CarrierDto> result = carrierService.getAllCarriers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(carrierDto.getCode(), result.getContent().get(0).getCode());
        verify(carrierRepository).findAll(pageable);
    }

    @Test
    void getCarrierById_shouldReturnCarrier_whenExists() {
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierMapper.toDto(carrier)).thenReturn(carrierDto);

        CarrierDto result = carrierService.getCarrierById(carrierId);

        assertNotNull(result);
        assertEquals(carrierId, result.getId());
        assertEquals("CAR001", result.getCode());
        verify(carrierRepository).findById(carrierId);
    }

    @Test
    void getCarrierById_shouldThrowException_whenNotFound() {
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> carrierService.getCarrierById(carrierId));
    }

    @Test
    void getCarrierByCode_shouldReturnCarrier_whenExists() {
        when(carrierRepository.findByCode("CAR001")).thenReturn(Optional.of(carrier));
        when(carrierMapper.toDto(carrier)).thenReturn(carrierDto);

        CarrierDto result = carrierService.getCarrierByCode("CAR001");

        assertNotNull(result);
        assertEquals("CAR001", result.getCode());
        verify(carrierRepository).findByCode("CAR001");
    }

    @Test
    void getCarrierByCode_shouldThrowException_whenNotFound() {
        when(carrierRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> carrierService.getCarrierByCode("INVALID"));
    }

    @Test
    void createCarrier_shouldCreateSuccessfully() {
        CarrierCreateDto createDto = new CarrierCreateDto();
        createDto.setCode("CAR002");
        createDto.setName("New Carrier");
        
        when(carrierRepository.existsByCode("CAR002")).thenReturn(false);
        when(carrierMapper.toEntity(createDto)).thenReturn(carrier);
        when(carrierRepository.save(carrier)).thenReturn(carrier);
        when(carrierMapper.toDto(carrier)).thenReturn(carrierDto);

        CarrierDto result = carrierService.createCarrier(createDto);

        assertNotNull(result);
        verify(carrierRepository).existsByCode("CAR002");
        verify(carrierRepository).save(carrier);
    }

    @Test
    void createCarrier_shouldThrowException_whenCodeExists() {
        CarrierCreateDto createDto = new CarrierCreateDto();
        createDto.setCode("CAR001");
        
        when(carrierRepository.existsByCode("CAR001")).thenReturn(true);

        assertThrows(ValidationException.class, 
            () -> carrierService.createCarrier(createDto));
        verify(carrierRepository, never()).save(any());
    }

    @Test
    void updateCarrier_shouldUpdateSuccessfully() {
        CarrierUpdateDto updateDto = new CarrierUpdateDto();
        updateDto.setName("Updated Carrier");
        
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        doNothing().when(carrierMapper).updateFromDto(updateDto, carrier);
        when(carrierRepository.save(carrier)).thenReturn(carrier);
        when(carrierMapper.toDto(carrier)).thenReturn(carrierDto);

        CarrierDto result = carrierService.updateCarrier(carrierId, updateDto);

        assertNotNull(result);
        verify(carrierMapper).updateFromDto(updateDto, carrier);
        verify(carrierRepository).save(carrier);
    }

    @Test
    void updateCarrier_shouldThrowException_whenNotFound() {
        CarrierUpdateDto updateDto = new CarrierUpdateDto();
        
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> carrierService.updateCarrier(carrierId, updateDto));
        verify(carrierRepository, never()).save(any());
    }

    @Test
    void updateCarrierStatus_shouldUpdateSuccessfully() {
        CarrierStatusUpdateDto statusDto = new CarrierStatusUpdateDto();
        statusDto.setStatus(CarrierStatus.INACTIVE);
        
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierRepository.save(carrier)).thenReturn(carrier);
        when(carrierMapper.toDto(carrier)).thenReturn(carrierDto);

        CarrierDto result = carrierService.updateCarrierStatus(carrierId, statusDto);

        assertNotNull(result);
        verify(carrierRepository).save(carrier);
    }

    @Test
    void deleteCarrier_shouldDeleteSuccessfully() {
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        doNothing().when(carrierRepository).delete(carrier);

        assertDoesNotThrow(() -> carrierService.deleteCarrier(carrierId));
        
        verify(carrierRepository).findById(carrierId);
        verify(carrierRepository).delete(carrier);
    }

    @Test
    void deleteCarrier_shouldThrowException_whenNotFound() {
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> carrierService.deleteCarrier(carrierId));
        verify(carrierRepository, never()).delete(any());
    }
}
