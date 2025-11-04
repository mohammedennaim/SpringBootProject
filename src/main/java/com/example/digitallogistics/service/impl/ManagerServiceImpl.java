package com.example.digitallogistics.service.impl;

import com.example.digitallogistics.model.entity.Manager;
import com.example.digitallogistics.model.enums.Role;
import com.example.digitallogistics.repository.ManagerRepository;
import com.example.digitallogistics.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Manager> findAll() {
        return managerRepository.findAll();
    }

    @Override
    public Optional<Manager> findById(UUID id) {
        return managerRepository.findById(id);
    }

    @Override
    public List<Manager> findByWarehouseId(UUID warehouseId) {
        return managerRepository.findByWarehouseId(warehouseId);
    }

    @Override
    public List<Manager> findActive() {
        return managerRepository.findByActiveTrue();
    }

    @Override
    public Manager create(Manager manager) {
        // Encoder le mot de passe
        manager.setPassword(passwordEncoder.encode(manager.getPassword()));
        // S'assurer que le rôle est WAREHOUSE_MANAGER
        manager.setRole(Role.WAREHOUSE_MANAGER);
        return managerRepository.save(manager);
    }

    @Override
    public Optional<Manager> update(UUID id, Manager manager) {
        return managerRepository.findById(id)
                .map(existingManager -> {
                    if (manager.getEmail() != null) {
                        existingManager.setEmail(manager.getEmail());
                    }
                    if (manager.getPassword() != null && !manager.getPassword().isEmpty()) {
                        existingManager.setPassword(passwordEncoder.encode(manager.getPassword()));
                    }
                    if (manager.getWarehouseId() != null) {
                        existingManager.setWarehouseId(manager.getWarehouseId());
                    }
                    existingManager.setActive(manager.isActive());
                    return managerRepository.save(existingManager);
                });
    }

    @Override
    public void delete(UUID id) {
        managerRepository.deleteById(id);
    }

    @Override
    public Optional<Manager> findByEmail(String email) {
        return managerRepository.findByEmail(email);
    }
}