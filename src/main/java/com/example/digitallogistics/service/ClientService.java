package com.example.digitallogistics.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.digitallogistics.model.entity.Client;

public interface ClientService {
    List<Client> findAll();
    Optional<Client> findById(UUID id);
    List<Client> findByNameContaining(String namePart);
    List<Client> findActive();
    Client create(Client client);
    Optional<Client> update(UUID id, Client client);
    void delete(UUID id);
}
