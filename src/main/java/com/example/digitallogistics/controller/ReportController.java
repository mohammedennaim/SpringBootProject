package com.example.digitallogistics.controller;

import com.example.digitallogistics.model.dto.*;
import com.example.digitallogistics.model.enums.MovementType;
import com.example.digitallogistics.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contrôleur pour les rapports et statistiques
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "API de génération de rapports et statistiques")
public class ReportController {
    
    private final ReportService reportService;

    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(
        summary = "Statistiques des commandes",
        description = "Génère un rapport détaillé sur les commandes avec taux de livraison, backorders, revenus, etc."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Rapport généré avec succès",
            content = @Content(schema = @Schema(implementation = OrderReportDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès interdit - rôle ADMIN ou WAREHOUSE_MANAGER requis"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la génération du rapport")
    })
    public ResponseEntity<OrderReportDto> getOrderReport(
            @Parameter(description = "Date de début (format: yyyy-MM-dd)", example = "2024-10-01")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            
            @Parameter(description = "Date de fin (format: yyyy-MM-dd)", example = "2024-11-04")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        try {
            OrderReportDto report = reportService.getOrderReport(fromDate, toDate);
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(
        summary = "Rapport d'inventaire",
        description = "Génère un rapport sur l'état des stocks, ruptures, surstocks, valeurs d'inventaire et rotation"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Rapport généré avec succès",
            content = @Content(schema = @Schema(implementation = InventoryReportDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès interdit - rôle ADMIN ou WAREHOUSE_MANAGER requis"),
        @ApiResponse(responseCode = "404", description = "Entrepôt non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la génération du rapport")
    })
    public ResponseEntity<InventoryReportDto> getInventoryReport(
            @Parameter(description = "ID de l'entrepôt (optionnel - si non fourni, tous les entrepôts)")
            @RequestParam(required = false) UUID warehouseId) {
        
        try {
            InventoryReportDto report = reportService.getInventoryReport(warehouseId);
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/shipments")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(
        summary = "Rapport d'expéditions",
        description = "Génère un rapport sur les performances d'expéditions, taux de livraison à temps, performance par transporteur"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Rapport généré avec succès",
            content = @Content(schema = @Schema(implementation = ShipmentReportDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès interdit - rôle ADMIN ou WAREHOUSE_MANAGER requis"),
        @ApiResponse(responseCode = "404", description = "Transporteur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la génération du rapport")
    })
    public ResponseEntity<ShipmentReportDto> getShipmentReport(
            @Parameter(description = "Date de début (format: yyyy-MM-dd)", example = "2024-10-01")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            
            @Parameter(description = "Date de fin (format: yyyy-MM-dd)", example = "2024-11-04")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            
            @Parameter(description = "ID du transporteur (optionnel - si non fourni, tous les transporteurs)")
            @RequestParam(required = false) UUID carrierId) {
                
        try {
            ShipmentReportDto report = reportService.getShipmentReport(fromDate, toDate, carrierId);
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
}