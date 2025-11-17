package com.example.digitallogistics.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.digitallogistics.model.dto.ClientCreateDto;
import com.example.digitallogistics.model.dto.ClientDto;
import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.model.mapper.ClientMapper;
import com.example.digitallogistics.service.ClientService;
import com.example.digitallogistics.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    @MockBean
    private ClientMapper clientMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void getAllClients_shouldReturnList() throws Exception {
        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setName("Test Client");

        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setName("Test Client");

        when(clientService.findAll()).thenReturn(List.of(client));
        when(clientMapper.toDto(any(Client.class))).thenReturn(dto);

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Client"));
    }

    @Test
    void createClient_shouldReturnCreated() throws Exception {
        ClientCreateDto createDto = new ClientCreateDto();
        createDto.setName("New Client");
        createDto.setContact("+1234567890");

        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setName("New Client");

        ClientDto responseDto = new ClientDto();
        responseDto.setId(client.getId());
        responseDto.setName("New Client");

        when(clientMapper.toEntity(any(ClientCreateDto.class))).thenReturn(client);
        when(clientService.create(any(Client.class))).thenReturn(client);
        when(clientMapper.toDto(any(Client.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Client"));
    }

    @Test
    void getClientById_shouldReturnClient() throws Exception {
        UUID id = UUID.randomUUID();
        Client client = new Client();
        client.setId(id);
        client.setName("Test Client");

        ClientDto dto = new ClientDto();
        dto.setId(id);
        dto.setName("Test Client");

        when(clientService.findById(eq(id))).thenReturn(Optional.of(client));
        when(clientMapper.toDto(any(Client.class))).thenReturn(dto);

        mockMvc.perform(get("/api/clients/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Client"));
    }

    @Test
    void getClientById_notFound_shouldReturn404() throws Exception {
        UUID id = UUID.randomUUID();
        when(clientService.findById(eq(id))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clients/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateClient_shouldReturnUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        Client client = new Client();
        client.setId(id);
        client.setName("Updated");
        ClientDto dto = new ClientDto();
        dto.setId(id);
        dto.setName("Updated");
        when(clientService.update(eq(id), any())).thenReturn(Optional.of(client));
        when(clientMapper.toDto(any())).thenReturn(dto);
        mockMvc.perform(put("/api/clients/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateClient_shouldReturn404_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(clientService.update(eq(id), any())).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/clients/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isNotFound());
    }


}
