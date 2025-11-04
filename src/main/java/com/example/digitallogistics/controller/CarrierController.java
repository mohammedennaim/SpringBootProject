package com.example.digitallogistics.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.model.dto.CarrierCreateDto;
import com.example.digitallogistics.model.dto.CarrierDto;
import com.example.digitallogistics.model.dto.CarrierStatusUpdateDto;
import com.example.digitallogistics.model.dto.CarrierUpdateDto;
import com.example.digitallogistics.model.enums.CarrierStatus;
import com.example.digitallogistics.service.CarrierService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carriers")
@RequiredArgsConstructor
@Tag(name = "Carriers", description = "API de gestion des transporteurs")
public class CarrierController {

    private final CarrierService carrierService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Liste des transporteurs", 
               description = "Récupère la liste paginée de tous les transporteurs avec filtres optionnels")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    public ResponseEntity<Page<CarrierDto>> getAllCarriers(
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Tri par champ", example = "name")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direction du tri", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDirection,
            @Parameter(description = "Filtrer par statut")
            @RequestParam(required = false) CarrierStatus status,
            @Parameter(description = "Filtrer par nom (recherche partielle)")
            @RequestParam(required = false) String name) {


        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CarrierDto> carriers;
        if (status != null || name != null) {
            carriers = carrierService.getCarriersByStatusAndName(status, name, pageable);
        } else {
            carriers = carrierService.getAllCarriers(pageable);
        }

        return ResponseEntity.ok(carriers);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Création d'un transporteur", 
               description = "Crée un nouveau transporteur avec les informations fournies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transporteur créé avec succès",
                    content = @Content(schema = @Schema(implementation = CarrierDto.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides ou code déjà existant"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    public ResponseEntity<CarrierDto> createCarrier(
            @Parameter(description = "Données de création du transporteur", required = true)
            @Valid @RequestBody CarrierCreateDto createDto) {
        CarrierDto createdCarrier = carrierService.createCarrier(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCarrier);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Détails du transporteur", 
               description = "Récupère les détails d'un transporteur par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transporteur trouvé",
                    content = @Content(schema = @Schema(implementation = CarrierDto.class))),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit"),
        @ApiResponse(responseCode = "404", description = "Transporteur non trouvé")
    })
    public ResponseEntity<CarrierDto> getCarrierById(
            @Parameter(description = "ID du transporteur", required = true)
            @PathVariable UUID id) {        
        CarrierDto carrier = carrierService.getCarrierById(id);
        return ResponseEntity.ok(carrier);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Mise à jour des infos", 
               description = "Met à jour les informations d'un transporteur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transporteur mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = CarrierDto.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit"),
        @ApiResponse(responseCode = "404", description = "Transporteur non trouvé")
    })
    public ResponseEntity<CarrierDto> updateCarrier(
            @Parameter(description = "ID du transporteur", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Nouvelles informations du transporteur", required = true)
            @Valid @RequestBody CarrierUpdateDto updateDto) {
        CarrierDto updatedCarrier = carrierService.updateCarrier(id, updateDto);
        return ResponseEntity.ok(updatedCarrier);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Activer / suspendre un transporteur", 
               description = "Met à jour le statut d'un transporteur (ACTIVE, INACTIVE, SUSPENDED)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = CarrierDto.class))),
        @ApiResponse(responseCode = "400", description = "Statut invalide"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit"),
        @ApiResponse(responseCode = "404", description = "Transporteur non trouvé")
    })
    public ResponseEntity<CarrierDto> updateCarrierStatus(
            @Parameter(description = "ID du transporteur", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Nouveau statut du transporteur", required = true)
            @Valid @RequestBody CarrierStatusUpdateDto statusUpdate) {
        CarrierDto updatedCarrier = carrierService.updateCarrierStatus(id, statusUpdate);
        return ResponseEntity.ok(updatedCarrier);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Transporteurs actifs", 
               description = "Récupère la liste de tous les transporteurs actifs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    public ResponseEntity<List<CarrierDto>> getAllActiveCarriers() {
        List<CarrierDto> activeCarriers = carrierService.getAllActiveCarriers();
        return ResponseEntity.ok(activeCarriers);
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Transporteur par code", 
               description = "Récupère un transporteur par son code unique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transporteur trouvé",
                    content = @Content(schema = @Schema(implementation = CarrierDto.class))),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit"),
        @ApiResponse(responseCode = "404", description = "Transporteur non trouvé")
    })
    public ResponseEntity<CarrierDto> getCarrierByCode(
            @Parameter(description = "Code du transporteur", required = true)
            @PathVariable String code) {
        CarrierDto carrier = carrierService.getCarrierByCode(code);
        return ResponseEntity.ok(carrier);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un transporteur", 
               description = "Supprime un transporteur (admin uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transporteur supprimé avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit"),
        @ApiResponse(responseCode = "404", description = "Transporteur non trouvé"),
        @ApiResponse(responseCode = "409", description = "Conflit - transporteur utilisé par des expéditions")
    })
    public ResponseEntity<Void> deleteCarrier(
            @Parameter(description = "ID du transporteur", required = true)
            @PathVariable UUID id) {
        carrierService.deleteCarrier(id);
        return ResponseEntity.noContent().build();
    }
}