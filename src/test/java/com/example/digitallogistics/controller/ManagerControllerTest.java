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

import com.example.digitallogistics.model.dto.ManagerCreateDto;
import com.example.digitallogistics.model.dto.ManagerDto;
import com.example.digitallogistics.model.entity.Manager;
import com.example.digitallogistics.model.mapper.ManagerMapper;
import com.example.digitallogistics.service.ManagerService;
import com.example.digitallogistics.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ManagerController.class)
@AutoConfigureMockMvc(addFilters = false)
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ManagerService managerService;

    @MockBean
    private ManagerMapper managerMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void getAllManagers_shouldReturnList() throws Exception {
        Manager manager = new Manager();
        manager.setId(UUID.randomUUID());
        manager.setEmail("manager@test.com");

        ManagerDto dto = new ManagerDto();
        dto.setId(manager.getId());
        dto.setEmail("manager@test.com");

        when(managerService.findAll()).thenReturn(List.of(manager));
        when(managerMapper.toDto(any(Manager.class))).thenReturn(dto);

        mockMvc.perform(get("/api/managers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("manager@test.com"));
    }

    @Test
    void createManager_shouldReturnCreated() throws Exception {
        ManagerCreateDto createDto = new ManagerCreateDto();
        createDto.setEmail("manager@example.com");
        createDto.setPassword("password123");

        Manager manager = new Manager();
        manager.setId(UUID.randomUUID());
        manager.setEmail("manager@example.com");

        ManagerDto responseDto = new ManagerDto();
        responseDto.setId(manager.getId());
        responseDto.setEmail("manager@example.com");

        when(managerService.findByEmail(any())).thenReturn(Optional.empty());
        when(managerMapper.toEntity(any(ManagerCreateDto.class))).thenReturn(manager);
        when(managerService.create(any(Manager.class))).thenReturn(manager);
        when(managerMapper.toDto(any(Manager.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/managers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("manager@example.com"));
    }

    @Test
    void getManagerById_shouldReturnManager() throws Exception {
        UUID id = UUID.randomUUID();
        Manager manager = new Manager();
        manager.setId(id);
        manager.setEmail("test@manager.com");

        ManagerDto dto = new ManagerDto();
        dto.setId(id);
        dto.setEmail("test@manager.com");

        when(managerService.findById(eq(id))).thenReturn(Optional.of(manager));
        when(managerMapper.toDto(any(Manager.class))).thenReturn(dto);

        mockMvc.perform(get("/api/managers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@manager.com"));
    }

    @Test
    void getActiveManagers_shouldReturnActiveList() throws Exception {
        Manager manager = new Manager();
        manager.setId(UUID.randomUUID());
        manager.setEmail("active@manager.com");
        manager.setActive(true);

        ManagerDto dto = new ManagerDto();
        dto.setId(manager.getId());
        dto.setEmail("active@manager.com");

        when(managerService.findActive()).thenReturn(List.of(manager));
        when(managerMapper.toDto(any(Manager.class))).thenReturn(dto);

        mockMvc.perform(get("/api/managers/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("active@manager.com"));
    }
}
