package com.example.digitallogistics.repository.impl;

import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.model.enums.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
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
class UserRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<User> typedQuery;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        // Setup is handled by @Mock and @InjectMocks annotations
    }

    @Test
    void findByRole_WithValidRole_ShouldReturnUsers() {
        Role role = Role.ADMIN;
        User user1 = new User();
        User user2 = new User();
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(entityManager.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("role", role)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedUsers);

        List<User> result = userRepository.findByRole(role);

        assertEquals(expectedUsers, result);
        assertEquals(2, result.size());
        verify(entityManager).createQuery("SELECT u FROM User u WHERE u.role = :role", User.class);
        verify(typedQuery).setParameter("role", role);
        verify(typedQuery).getResultList();
    }

    @Test
    void findByRole_WithNullRole_ShouldReturnEmptyList() {
        List<User> result = userRepository.findByRole(null);

        assertTrue(result.isEmpty());
        verify(entityManager, never()).createQuery(anyString(), any(Class.class));
    }

    @Test
    void findByRole_WhenQueryThrowsException_ShouldReturnEmptyList() {
        Role role = Role.CLIENT;

        when(entityManager.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class))
                .thenThrow(new IllegalArgumentException("Query error"));

        List<User> result = userRepository.findByRole(role);

        assertTrue(result.isEmpty());
        verify(entityManager).createQuery("SELECT u FROM User u WHERE u.role = :role", User.class);
    }

    @Test
    void findByRole_WithWarehouseManagerRole_ShouldReturnManagers() {
        Role role = Role.WAREHOUSE_MANAGER;
        User manager = new User();
        List<User> expectedUsers = Collections.singletonList(manager);

        when(entityManager.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("role", role)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedUsers);

        List<User> result = userRepository.findByRole(role);

        assertEquals(expectedUsers, result);
        assertEquals(1, result.size());
    }

    @Test
    void findByRole_WithClientRole_ShouldReturnClients() {
        Role role = Role.CLIENT;
        List<User> expectedUsers = Collections.emptyList();

        when(entityManager.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("role", role)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedUsers);

        List<User> result = userRepository.findByRole(role);

        assertTrue(result.isEmpty());
    }
}