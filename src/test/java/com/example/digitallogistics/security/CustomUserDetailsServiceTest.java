package com.example.digitallogistics.security;

import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.model.enums.Role;
import com.example.digitallogistics.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CLIENT);
        user.setActive(true);
    }

    @Test
    void loadUserByUsername_WithValidEmail_ShouldReturnUserDetails() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT")));
        
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_WithInactiveUser_ShouldReturnDisabledUserDetails() {
        user.setActive(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_WithNonExistentEmail_ShouldThrowException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> 
            customUserDetailsService.loadUserByUsername("nonexistent@example.com"));
        
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void loadUserByUsername_WithAdminRole_ShouldReturnAdminAuthority() {
        user.setRole(Role.ADMIN);
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin@example.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_WithWarehouseManagerRole_ShouldReturnWarehouseManagerAuthority() {
        user.setRole(Role.WAREHOUSE_MANAGER);
        when(userRepository.findByEmail("manager@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("manager@example.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_WAREHOUSE_MANAGER")));
    }
}