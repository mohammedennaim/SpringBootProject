package com.example.digitallogistics.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.model.enums.Role;
import com.example.digitallogistics.repository.ClientRepository;
import com.example.digitallogistics.service.ClientService;
import com.example.digitallogistics.service.UserService;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.userService = userService;
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
        // ensure client has an id before save (entity also has @PrePersist, but keep defensive)
        if (client.getId() == null) {
            client.setId(UUID.randomUUID());
        }

        Client saved = clientRepository.save(client);

        UUID id = saved.getId();
        if (id != null) {
            if (userService.findById(id).isEmpty()) {
                User u = new User();
                String raw = UUID.randomUUID().toString();
                u.setId(id);
                u.setPassword(passwordEncoder.encode(raw));
                u.setRole(Role.CLIENT);
                u.setActive(Boolean.TRUE.equals(client.getActive()));
                userService.create(u);
            }
        }

        return saved;
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
