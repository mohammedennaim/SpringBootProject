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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CarrierServiceImpl implements CarrierService {

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CarrierDto> getAllCarriers(Pageable pageable) {
        log.debug("Retrieving all carriers with pagination: {}", pageable);
        Page<Carrier> carriers = carrierRepository.findAll(pageable);
        return carriers.map(carrierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierDto getCarrierById(UUID id) {
        log.debug("Retrieving carrier by id: {}", id);
        Carrier carrier = carrierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + id));
        return carrierMapper.toDto(carrier);
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierDto getCarrierByCode(String code) {
        log.debug("Retrieving carrier by code: {}", code);
        Carrier carrier = carrierRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with code: " + code));
        return carrierMapper.toDto(carrier);
    }

    @Override
    public CarrierDto createCarrier(CarrierCreateDto createDto) {
        log.debug("Creating new carrier with code: {}", createDto.getCode());
        
        // Vérifier que le code n'existe pas déjà
        if (carrierRepository.existsByCode(createDto.getCode())) {
            throw new ValidationException("Carrier with code already exists: " + createDto.getCode());
        }

        // Créer le transporteur
        Carrier carrier = carrierMapper.toEntity(createDto);
        carrier.setStatus(CarrierStatus.ACTIVE); // Par défaut actif

        Carrier savedCarrier = carrierRepository.save(carrier);
        log.info("Created new carrier with id: {} and code: {}", savedCarrier.getId(), savedCarrier.getCode());
        
        return carrierMapper.toDto(savedCarrier);
    }

    @Override
    public CarrierDto updateCarrier(UUID id, CarrierUpdateDto updateDto) {
        log.debug("Updating carrier with id: {}", id);
        
        Carrier carrier = carrierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + id));

        // Mettre à jour les champs
        carrierMapper.updateFromDto(updateDto, carrier);

        Carrier updatedCarrier = carrierRepository.save(carrier);
        log.info("Updated carrier with id: {}", id);
        
        return carrierMapper.toDto(updatedCarrier);
    }

    @Override
    public CarrierDto updateCarrierStatus(UUID id, CarrierStatusUpdateDto statusUpdate) {
        log.debug("Updating carrier status to: {} for carrier: {}", statusUpdate.getStatus(), id);
        
        Carrier carrier = carrierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + id));

        // Valider la transition de statut
        validateStatusTransition(carrier.getStatus(), statusUpdate.getStatus());

        carrier.setStatus(statusUpdate.getStatus());

        Carrier updatedCarrier = carrierRepository.save(carrier);
        log.info("Updated carrier {} status to: {}", id, statusUpdate.getStatus());
        
        return carrierMapper.toDto(updatedCarrier);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarrierDto> getCarriersByStatus(CarrierStatus status, Pageable pageable) {
        log.debug("Retrieving carriers by status: {}", status);
        Page<Carrier> carriers = carrierRepository.findByStatus(status, pageable);
        return carriers.map(carrierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarrierDto> getCarriersByName(String name, Pageable pageable) {
        log.debug("Retrieving carriers by name containing: {}", name);
        Page<Carrier> carriers = carrierRepository.findByNameContainingIgnoreCase(name, pageable);
        return carriers.map(carrierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarrierDto> getCarriersByStatusAndName(
            CarrierStatus status, String name, Pageable pageable) {
        log.debug("Retrieving carriers by status: {} and name: {}", status, name);
        Page<Carrier> carriers = carrierRepository.findByStatusAndNameContaining(status, name, pageable);
        return carriers.map(carrierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarrierDto> getAllActiveCarriers() {
        log.debug("Retrieving all active carriers");
        List<Carrier> carriers = carrierRepository.findAllActive();
        return carriers.stream()
                .map(carrierMapper::toDto)
                .toList();
    }

    @Override
    public void deleteCarrier(UUID id) {
        log.debug("Deleting carrier: {}", id);
        
        Carrier carrier = carrierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + id));

        // Vérifier que le transporteur peut être supprimé
        // TODO: Vérifier qu'il n'y a pas d'expéditions en cours avec ce transporteur
        
        carrierRepository.delete(carrier);
        log.info("Deleted carrier: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return carrierRepository.existsByCode(code);
    }

    /**
     * Valide les transitions de statut autorisées
     */
    private void validateStatusTransition(CarrierStatus currentStatus, CarrierStatus newStatus) {
        if (currentStatus == newStatus) {
            return; // Pas de changement
        }

        // Toutes les transitions sont autorisées pour les transporteurs
        // ACTIVE <-> INACTIVE <-> SUSPENDED
        log.debug("Status transition from {} to {} is allowed", currentStatus, newStatus);
    }
}