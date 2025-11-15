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

import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.model.enums.Role;
import com.example.digitallogistics.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        
        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(Role.CLIENT);
        user.setActive(true);
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userRepository).findById(userId);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(userId);

        assertFalse(result.isPresent());
        verify(userRepository).findById(userId);
    }

    @Test
    void findByEmail_shouldReturnUser_whenExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("notfound@example.com");

        assertFalse(result.isPresent());
        verify(userRepository).findByEmail("notfound@example.com");
    }

    @Test
    void findByRole_shouldReturnUsersWithRole() {
        when(userRepository.findByRole(Role.CLIENT)).thenReturn(List.of(user));

        List<User> result = userService.findByRole(Role.CLIENT);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Role.CLIENT, result.get(0).getRole());
        verify(userRepository).findByRole(Role.CLIENT);
    }

    @Test
    void create_shouldSaveAndReturnUser() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setPassword("pass");
        
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User savedUser = i.getArgument(0);
            if (savedUser.getId() == null) {
                savedUser.setId(UUID.randomUUID());
            }
            return savedUser;
        });

        User result = userService.create(newUser);

        assertNotNull(result);
        assertNotNull(result.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_shouldUpdateExistingUser() {
        User updateData = new User();
        updateData.setEmail("updated@example.com");
        updateData.setPassword("newpass");
        updateData.setRole(Role.ADMIN);
        updateData.setActive(false);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        Optional<User> result = userService.update(userId, updateData);

        assertTrue(result.isPresent());
        assertEquals("updated@example.com", result.get().getEmail());
        assertEquals("newpass", result.get().getPassword());
        assertEquals(Role.ADMIN, result.get().getRole());
        assertFalse(result.get().isActive());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_shouldReturnEmpty_whenUserNotFound() {
        User updateData = new User();
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.update(userId, updateData);

        assertFalse(result.isPresent());
        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.delete(userId));
        
        verify(userRepository).deleteById(userId);
    }
}
