package com.example.digitallogistics.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.model.dto.ClientCreateDto;
import com.example.digitallogistics.model.dto.ClientDto;
import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.service.ClientService;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<ClientDto> list(@RequestParam(required = false) String name) {
        List<Client> clients = (name == null || name.isBlank()) ? clientService.findAll() : clientService.findByNameContaining(name);
        return clients.stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/active")
    public List<ClientDto> active() {
        return clientService.findActive().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> get(@PathVariable UUID id) {
        Optional<Client> c = clientService.findById(id);
        return c.map(this::toDto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClientDto> create(@RequestBody ClientCreateDto dto) {
        Client c = new Client();
        c.setName(dto.getName());
        c.setContact(dto.getContact());
        c.setActive(dto.getActive() == null ? Boolean.TRUE : dto.getActive());
        Client saved = clientService.create(c);
        return ResponseEntity.created(URI.create("/api/clients/" + saved.getId())).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> update(@PathVariable UUID id, @RequestBody ClientCreateDto dto) {
        Client c = new Client();
        c.setName(dto.getName());
        c.setContact(dto.getContact());
        c.setActive(dto.getActive());
        return clientService.update(id, c).map(this::toDto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ClientDto toDto(Client c) {
        return new ClientDto(c.getId(), c.getName(), c.getContact(), c.getActive());
    }
}
