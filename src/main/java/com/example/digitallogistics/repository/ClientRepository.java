package com.example.digitallogistics.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.Client;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    List<Client> findByNameContainingIgnoreCase(String namePart);
    List<Client> findByActiveTrue();
}
