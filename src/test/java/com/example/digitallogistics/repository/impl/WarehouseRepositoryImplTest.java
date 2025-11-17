package com.example.digitallogistics.repository.impl;

import com.example.digitallogistics.model.entity.Warehouse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Warehouse> typedQuery;

    @InjectMocks
    private WarehouseRepositoryImpl warehouseRepository;

    @Test
    void findAll_ShouldReturnAllWarehouses() {
        Warehouse warehouse1 = new Warehouse();
        Warehouse warehouse2 = new Warehouse();
        List<Warehouse> expectedWarehouses = Arrays.asList(warehouse1, warehouse2);

        when(entityManager.createQuery("SELECT w FROM Warehouse w", Warehouse.class))
                .thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedWarehouses);

        List<Warehouse> result = warehouseRepository.findAll();

        assertEquals(expectedWarehouses, result);
        assertEquals(2, result.size());
        verify(entityManager).createQuery("SELECT w FROM Warehouse w", Warehouse.class);
        verify(typedQuery).getResultList();
    }

    @Test
    void findAll_WhenQueryThrowsException_ShouldReturnEmptyList() {
        when(entityManager.createQuery("SELECT w FROM Warehouse w", Warehouse.class))
                .thenThrow(new IllegalArgumentException("Query error"));

        List<Warehouse> result = warehouseRepository.findAll();

        assertTrue(result.isEmpty());
        verify(entityManager).createQuery("SELECT w FROM Warehouse w", Warehouse.class);
    }

    @Test
    void findAll_WhenNoWarehouses_ShouldReturnEmptyList() {
        when(entityManager.createQuery("SELECT w FROM Warehouse w", Warehouse.class))
                .thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        List<Warehouse> result = warehouseRepository.findAll();

        assertTrue(result.isEmpty());
        verify(entityManager).createQuery("SELECT w FROM Warehouse w", Warehouse.class);
        verify(typedQuery).getResultList();
    }
}