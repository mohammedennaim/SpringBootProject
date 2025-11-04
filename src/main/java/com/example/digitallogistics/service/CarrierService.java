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

    /**
     * Récupère tous les transporteurs avec pagination
     */
    Page<CarrierDto> getAllCarriers(Pageable pageable);

    /**
     * Récupère un transporteur par son ID
     */
    CarrierDto getCarrierById(UUID id);

    /**
     * Récupère un transporteur par son code
     */
    CarrierDto getCarrierByCode(String code);

    /**
     * Crée un nouveau transporteur
     */
    CarrierDto createCarrier(CarrierCreateDto createDto);

    /**
     * Met à jour les informations d'un transporteur
     */
    CarrierDto updateCarrier(UUID id, CarrierUpdateDto updateDto);

    /**
     * Met à jour le statut d'un transporteur (activer/suspendre)
     */
    CarrierDto updateCarrierStatus(UUID id, CarrierStatusUpdateDto statusUpdate);

    /**
     * Recherche des transporteurs par statut
     */
    Page<CarrierDto> getCarriersByStatus(CarrierStatus status, Pageable pageable);

    /**
     * Recherche des transporteurs par nom (partiel)
     */
    Page<CarrierDto> getCarriersByName(String name, Pageable pageable);

    /**
     * Recherche combinée par statut et nom
     */
    Page<CarrierDto> getCarriersByStatusAndName(
        CarrierStatus status, String name, Pageable pageable);

    /**
     * Récupère tous les transporteurs actifs
     */
    List<CarrierDto> getAllActiveCarriers();

    /**
     * Supprime un transporteur
     */
    void deleteCarrier(UUID id);

    /**
     * Vérifie si un code transporteur existe déjà
     */
    boolean existsByCode(String code);
}