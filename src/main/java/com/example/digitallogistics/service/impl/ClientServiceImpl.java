package com.example.digitallogistics.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.model.enums.Role;
import com.example.digitallogistics.repository.ClientRepository;
import com.example.digitallogistics.service.ClientService;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Optional<Client> findById(UUID id) {
        return clientRepository.findById(id);
    }

    @Override
    public List<Client> findByNameContaining(String namePart) {
        return clientRepository.findByNameContainingIgnoreCase(namePart);
    }

    @Override
    public List<Client> findActive() {
        return clientRepository.findByActiveTrue();
    }

    @Override
    public Client create(Client client) {
        if (client.getPassword() == null || client.getPassword().isBlank()) {
            String raw = UUID.randomUUID().toString();
            client.setPassword(passwordEncoder.encode(raw));
        }
        client.setRole(Role.CLIENT);
        if (client.getActive() == null) {
            client.setActive(true);
        }

        if (client.getContact() != null && client.getContact().contains("@") && client.getEmail() == null) {
            client.setEmail(client.getContact());
        }

        return clientRepository.save(client);
    }

    @Override
    public Optional<Client> update(UUID id, Client client) {
        return clientRepository.findById(id).map(existing -> {
            existing.setName(client.getName());
            existing.setContact(client.getContact());
            existing.setActive(client.getActive());
            return clientRepository.save(existing);
        });
    }

    @Override
    public void delete(UUID id) {
        clientRepository.deleteById(id);
    }
}
