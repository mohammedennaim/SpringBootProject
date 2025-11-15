package com.example.digitallogistics.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.digitallogistics.model.entity.Manager;
import com.example.digitallogistics.model.entity.Warehouse;
import com.example.digitallogistics.model.enums.Role;
import com.example.digitallogistics.repository.ManagerRepository;
import com.example.digitallogistics.repository.WarehouseRepository;

@ExtendWith(MockitoExtension.class)
class ManagerServiceImplTest {

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ManagerServiceImpl managerService;

    private UUID managerId;
    private UUID warehouseId;
    private Manager manager;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        managerId = UUID.randomUUID();
        warehouseId = UUID.randomUUID();
        
        warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        warehouse.setName("Test Warehouse");
        warehouse.setCode("WH001");
        
        manager = new Manager();
        manager.setId(managerId);
        manager.setEmail("manager@example.com");
        manager.setPassword("password123");
        manager.setRole(Role.WAREHOUSE_MANAGER);
        manager.setActive(true);
        // Manager has warehouses (List), not warehouse
    }

    @Test
    void findAll_shouldReturnAllManagers() {
        when(managerRepository.findAll()).thenReturn(List.of(manager));

        List<Manager> result = managerService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("manager@example.com", result.get(0).getEmail());
        verify(managerRepository).findAll();
    }

    @Test
    void findById_shouldReturnManager_whenExists() {
        when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));

        Optional<Manager> result = managerService.findById(managerId);

        assertTrue(result.isPresent());
        assertEquals(managerId, result.get().getId());
        verify(managerRepository).findById(managerId);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        when(managerRepository.findById(managerId)).thenReturn(Optional.empty());

        Optional<Manager> result = managerService.findById(managerId);

        assertFalse(result.isPresent());
        verify(managerRepository).findById(managerId);
    }

    @Test
    void findByWarehouseId_shouldReturnManagersForWarehouse() {
        when(managerRepository.findByWarehouseId(warehouseId)).thenReturn(List.of(manager));

        List<Manager> result = managerService.findByWarehouseId(warehouseId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(managerRepository).findByWarehouseId(warehouseId);
    }

    @Test
    void findActive_shouldReturnOnlyActiveManagers() {
        when(managerRepository.findByActiveTrue()).thenReturn(List.of(manager));

        List<Manager> result = managerService.findActive();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(managerRepository).findByActiveTrue();
    }

    @Test
    void create_shouldEncodePasswordAndSetRole() {
        Manager newManager = new Manager();
        newManager.setEmail("new@example.com");
        newManager.setPassword("rawPassword");
        
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(managerRepository.save(any(Manager.class))).thenAnswer(i -> i.getArgument(0));

        Manager result = managerService.create(newManager);

        assertNotNull(result);
        assertEquals(Role.WAREHOUSE_MANAGER, result.getRole());
        assertEquals("encodedPassword", result.getPassword());
        verify(passwordEncoder).encode("rawPassword");
        verify(managerRepository).save(any(Manager.class));
    }

    @Test
    void update_shouldUpdateExistingManager() {
        Manager updateData = new Manager();
        updateData.setEmail("updated@example.com");
        updateData.setPassword("newPassword");
        updateData.setActive(false);
        
        when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(managerRepository.save(any(Manager.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Manager> result = managerService.update(managerId, updateData);

        assertTrue(result.isPresent());
        assertEquals("updated@example.com", result.get().getEmail());
        assertEquals("encodedNewPassword", result.get().getPassword());
        assertFalse(result.get().isActive());
        verify(passwordEncoder).encode("newPassword");
        verify(managerRepository).save(any(Manager.class));
    }

    @Test
    void update_shouldNotEncodePassword_whenPasswordIsNull() {
        Manager updateData = new Manager();
        updateData.setEmail("updated@example.com");
        updateData.setPassword(null);
        updateData.setActive(false);
        
        when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(managerRepository.save(any(Manager.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Manager> result = managerService.update(managerId, updateData);

        assertTrue(result.isPresent());
        assertEquals("updated@example.com", result.get().getEmail());
        assertEquals("password123", result.get().getPassword()); // Original password unchanged
        verify(passwordEncoder, never()).encode(anyString());
        verify(managerRepository).save(any(Manager.class));
    }

    @Test
    void update_shouldNotEncodePassword_whenPasswordIsEmpty() {
        Manager updateData = new Manager();
        updateData.setEmail("updated@example.com");
        updateData.setPassword("");
        updateData.setActive(false);
        
        when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(managerRepository.save(any(Manager.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Manager> result = managerService.update(managerId, updateData);

        assertTrue(result.isPresent());
        verify(passwordEncoder, never()).encode(anyString());
        verify(managerRepository).save(any(Manager.class));
    }

    @Test
    void update_shouldReturnEmpty_whenManagerNotFound() {
        Manager updateData = new Manager();
        
        when(managerRepository.findById(managerId)).thenReturn(Optional.empty());

        Optional<Manager> result = managerService.update(managerId, updateData);

        assertFalse(result.isPresent());
        verify(managerRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteManager() {
        doNothing().when(managerRepository).deleteById(managerId);

        assertDoesNotThrow(() -> managerService.delete(managerId));
        
        verify(managerRepository).deleteById(managerId);
    }

    @Test
    void findByEmail_shouldReturnManager_whenExists() {
        when(managerRepository.findByEmail("manager@example.com")).thenReturn(Optional.of(manager));

        Optional<Manager> result = managerService.findByEmail("manager@example.com");

        assertTrue(result.isPresent());
        assertEquals("manager@example.com", result.get().getEmail());
        verify(managerRepository).findByEmail("manager@example.com");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenNotFound() {
        when(managerRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<Manager> result = managerService.findByEmail("notfound@example.com");

        assertFalse(result.isPresent());
        verify(managerRepository).findByEmail("notfound@example.com");
    }
}
