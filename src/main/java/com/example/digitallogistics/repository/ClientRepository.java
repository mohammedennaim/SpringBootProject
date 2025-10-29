package com.example.digitallogistics.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByNameContainingIgnoreCase(String namePart);
    List<Client> findByActiveTrue();
}
