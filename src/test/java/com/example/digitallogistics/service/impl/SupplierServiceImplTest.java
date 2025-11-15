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

import com.example.digitallogistics.model.entity.Supplier;
import com.example.digitallogistics.repository.SupplierRepository;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private UUID supplierId;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplierId = UUID.randomUUID();
        
        supplier = new Supplier();
        supplier.setId(supplierId);
        supplier.setName("Test Supplier");
        supplier.setContact("contact@supplier.com");
    }

    @Test
    void findAll_shouldReturnAllSuppliers() {
        when(supplierRepository.findAll()).thenReturn(List.of(supplier));

        List<Supplier> result = supplierService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Supplier", result.get(0).getName());
        verify(supplierRepository).findAll();
    }

    @Test
    void findById_shouldReturnSupplier_whenExists() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));

        Optional<Supplier> result = supplierService.findById(supplierId);

        assertTrue(result.isPresent());
        assertEquals(supplierId, result.get().getId());
        verify(supplierRepository).findById(supplierId);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        Optional<Supplier> result = supplierService.findById(supplierId);

        assertFalse(result.isPresent());
        verify(supplierRepository).findById(supplierId);
    }

    @Test
    void findByNameContaining_shouldReturnMatchingSuppliers() {
        when(supplierRepository.findByNameContainingIgnoreCase("Test"))
            .thenReturn(List.of(supplier));

        List<Supplier> result = supplierService.findByNameContaining("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().contains("Test"));
        verify(supplierRepository).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void create_shouldSaveAndReturnSupplier() {
        when(supplierRepository.save(supplier)).thenReturn(supplier);

        Supplier result = supplierService.create(supplier);

        assertNotNull(result);
        assertEquals("Test Supplier", result.getName());
        verify(supplierRepository).save(supplier);
    }

    @Test
    void update_shouldUpdateExistingSupplier() {
        Supplier updatedSupplier = new Supplier();
        updatedSupplier.setName("Updated Supplier");
        updatedSupplier.setContact("new@supplier.com");
        
        when(supplierRepository.existsById(supplierId)).thenReturn(true);
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Supplier> result = supplierService.update(supplierId, updatedSupplier);

        assertTrue(result.isPresent());
        assertEquals(supplierId, result.get().getId());
        assertEquals("Updated Supplier", result.get().getName());
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    void update_shouldReturnEmpty_whenSupplierNotFound() {
        Supplier updatedSupplier = new Supplier();
        
        when(supplierRepository.existsById(supplierId)).thenReturn(false);

        Optional<Supplier> result = supplierService.update(supplierId, updatedSupplier);

        assertFalse(result.isPresent());
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void deleteById_shouldDeleteSupplier() {
        doNothing().when(supplierRepository).deleteById(supplierId);

        assertDoesNotThrow(() -> supplierService.deleteById(supplierId));
        
        verify(supplierRepository).deleteById(supplierId);
    }
}
