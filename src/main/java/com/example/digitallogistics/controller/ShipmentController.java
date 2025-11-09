package com.example.digitallogistics.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.model.dto.ShipmentCreateDto;
import com.example.digitallogistics.model.dto.ShipmentDto;
import com.example.digitallogistics.model.dto.ShipmentStatusUpdateDto;
import com.example.digitallogistics.model.enums.ShipmentStatus;
import com.example.digitallogistics.service.ShipmentService;

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
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipments", description = "API de gestion des expéditions")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping
    @SuppressWarnings("null")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Liste des expéditions", 
               description = "Récupère la liste paginée de toutes les expéditions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    public ResponseEntity<Page<ShipmentDto>> getAllShipments(
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Tri par champ", example = "shippedAt")
            @RequestParam(defaultValue = "shippedAt") String sortBy,
            @Parameter(description = "Direction du tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection,
            @Parameter(description = "Filtrer par statut")
            @RequestParam(required = false) ShipmentStatus status,
            @Parameter(description = "Filtrer par entrepôt")
            @RequestParam(required = false) UUID warehouseId) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ShipmentDto> shipments;
        if (status != null || warehouseId != null) {
            shipments = shipmentService.getShipmentsByStatusAndWarehouse(status, warehouseId, pageable);
        } else {
            shipments = shipmentService.getAllShipments(pageable);
        }

        return ResponseEntity.ok(shipments);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Créer une expédition", 
               description = "Crée une nouvelle expédition pour une commande RESERVED")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Expédition créée avec succès",
                    content = @Content(schema = @Schema(implementation = ShipmentDto.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit"),
        @ApiResponse(responseCode = "404", description = "Commande, entrepôt ou transporteur non trouvé"),
        @ApiResponse(responseCode = "409", description = "La commande a déjà une expédition")
    })
    public ResponseEntity<ShipmentDto> createShipment(
            @Parameter(description = "Données de création de l'expédition", required = true)
            @Valid @RequestBody ShipmentCreateDto createDto) {
        ShipmentDto createdShipment = shipmentService.createShipment(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShipment);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'CLIENT')")
    @Operation(summary = "Détails de l'expédition", 
               description = "Récupère les détails d'une expédition par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expédition trouvée",
                    content = @Content(schema = @Schema(implementation = ShipmentDto.class))),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit"),
        @ApiResponse(responseCode = "404", description = "Expédition non trouvée")
    })
    public ResponseEntity<ShipmentDto> getShipmentById(
            @Parameter(description = "ID de l'expédition", required = true)
            @PathVariable UUID id) {
        ShipmentDto shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(shipment);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Mise à jour du statut", 
               description = "Met à jour le statut d'une expédition (PLANNED → IN_TRANSIT → DELIVERED)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = ShipmentDto.class))),
        @ApiResponse(responseCode = "400", description = "Transition de statut invalide"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit"),
        @ApiResponse(responseCode = "404", description = "Expédition non trouvée")
    })
    public ResponseEntity<ShipmentDto> updateShipmentStatus(
            @Parameter(description = "ID de l'expédition", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Nouveau statut de l'expédition", required = true)
            @Valid @RequestBody ShipmentStatusUpdateDto statusUpdate) {
        ShipmentDto updatedShipment = shipmentService.updateShipmentStatus(id, statusUpdate);
        return ResponseEntity.ok(updatedShipment);
    }

    @GetMapping("/tracking/{trackingNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'CLIENT')")
    @Operation(summary = "Suivi par numéro", 
               description = "Récupère une expédition par son numéro de suivi")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expédition trouvée",
                    content = @Content(schema = @Schema(implementation = ShipmentDto.class))),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "404", description = "Expédition non trouvée")
    })
    public ResponseEntity<ShipmentDto> getShipmentByTrackingNumber(
            @Parameter(description = "Numéro de suivi", required = true)
            @PathVariable String trackingNumber) {
        ShipmentDto shipment = shipmentService.getShipmentByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(shipment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une expédition", 
               description = "Supprime une expédition (non livrée uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Expédition supprimée avec succès"),
        @ApiResponse(responseCode = "400", description = "Impossible de supprimer une expédition livrée"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit"),
        @ApiResponse(responseCode = "404", description = "Expédition non trouvée")
    })
    public ResponseEntity<Void> deleteShipment(
            @Parameter(description = "ID de l'expédition", required = true)
            @PathVariable UUID id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }
}