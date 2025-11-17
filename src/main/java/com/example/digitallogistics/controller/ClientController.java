package com.example.digitallogistics.controller;

import com.example.digitallogistics.model.dto.ClientCreateDto;
import com.example.digitallogistics.model.dto.ClientDto;
import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.model.mapper.ClientMapper;
import com.example.digitallogistics.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "API de gestion des clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientMapper clientMapper;

    @GetMapping
    @Operation(summary = "Obtenir la liste de tous les clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<List<ClientDto>> getAllClients() {
        List<Client> clients = clientService.findAll();
        List<ClientDto> clientDtos = clients.stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientDtos);
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau client")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<ClientDto> createClient(@Valid @RequestBody ClientCreateDto clientCreateDto) {
        Client client = clientMapper.toEntity(clientCreateDto);
        Client createdClient = clientService.create(client);
        ClientDto clientDto = clientMapper.toDto(createdClient);
        return new ResponseEntity<>(clientDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un client")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'DRIVER')")
    public ResponseEntity<ClientDto> getClientById(@PathVariable UUID id) {
        return clientService.findById(id)
                .map(client -> ResponseEntity.ok(clientMapper.toDto(client)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour les informations d'un client")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<ClientDto> updateClient(@PathVariable UUID id, @Valid @RequestBody ClientCreateDto clientCreateDto) {
        Client client = clientMapper.toEntity(clientCreateDto);
        return clientService.update(id, client)
                .map(updated -> ResponseEntity.ok(clientMapper.toDto(updated)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}