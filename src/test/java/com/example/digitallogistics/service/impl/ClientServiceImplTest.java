package com.example.digitallogistics.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.model.enums.Role;
import com.example.digitallogistics.repository.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientServiceImpl clientService;

    private UUID clientId;
    private Client client;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        client = new Client();
        client.setId(clientId);
        client.setName("Test Client");
        client.setContact("+212600000000");
        client.setEmail("test@client.com");
        client.setActive(true);
    }

    @Test
    void findAll_shouldReturnAllClients() {
        when(clientRepository.findAll()).thenReturn(List.of(client));

        List<Client> result = clientService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Client", result.get(0).getName());
        verify(clientRepository).findAll();
    }

    @Test
    void findById_shouldReturnClient() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        Optional<Client> result = clientService.findById(clientId);

        assertTrue(result.isPresent());
        assertEquals(clientId, result.get().getId());
        verify(clientRepository).findById(clientId);
    }

    @Test
    void findByNameContaining_shouldReturnMatchingClients() {
        when(clientRepository.findByNameContainingIgnoreCase("Test")).thenReturn(List.of(client));

        List<Client> result = clientService.findByNameContaining("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().contains("Test"));
    }

    @Test
    void findActive_shouldReturnOnlyActiveClients() {
        Client activeClient = new Client();
        activeClient.setId(clientId);
        activeClient.setName("Active Client");
        activeClient.setActive(Boolean.TRUE);
        
        when(clientRepository.findByActiveTrue()).thenReturn(List.of(activeClient));

        List<Client> result = clientService.findActive();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Boolean.TRUE, result.get(0).getActive());
    }

    @Test
    void create_shouldEncodePasswordAndSetRole() {
        Client newClient = new Client();
        newClient.setName("New Client");
        newClient.setContact("contact@test.com");
        
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0);
            // Simulate that the repository returns the saved entity with an ID
            savedClient.setId(UUID.randomUUID());
            return savedClient;
        });

        Client result = clientService.create(newClient);

        assertNotNull(result);
        assertEquals(Role.CLIENT, result.getRole());
        assertNotNull(result.getPassword());
        // Verify active is set (Note: Client inherits boolean active from User, which defaults to false)
        // The service sets it via setActive(true)
        verify(passwordEncoder).encode(anyString());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void create_shouldSetEmailFromContact_whenContactIsEmail() {
        Client newClient = new Client();
        newClient.setName("Email Client");
        newClient.setContact("email@example.com");
        
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        Client result = clientService.create(newClient);

        assertEquals("email@example.com", result.getEmail());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void update_shouldUpdateExistingClient() {
        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setName("Old Name");
        existingClient.setContact("old@contact.com");
        existingClient.setActive(Boolean.TRUE);
        
        Client updates = new Client();
        updates.setName("Updated Name");
        updates.setContact("updated@contact.com");
        updates.setActive(Boolean.FALSE);

        when(clientRepository.existsById(clientId)).thenReturn(true);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Client> result = clientService.update(clientId, updates);

        assertTrue(result.isPresent());
        assertEquals("Updated Name", result.get().getName());
        assertEquals("updated@contact.com", result.get().getContact());
        assertEquals(Boolean.FALSE, result.get().getActive());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void update_shouldThrowException_whenClientNotFound() {
        when(clientRepository.existsById(clientId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, 
            () -> clientService.update(clientId, new Client()));
    }

    @Test
    void delete_shouldDeleteClient() {
        when(clientRepository.existsById(clientId)).thenReturn(true);

        clientService.delete(clientId);

        verify(clientRepository).deleteById(clientId);
    }

    @Test
    void delete_shouldThrowException_whenClientNotFound() {
        when(clientRepository.existsById(clientId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, 
            () -> clientService.delete(clientId));
    }
}
