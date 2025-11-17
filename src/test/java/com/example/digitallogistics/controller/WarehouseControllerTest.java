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

import com.example.digitallogistics.model.dto.WarehouseCreateDto;
import com.example.digitallogistics.model.dto.WarehouseDto;
import com.example.digitallogistics.model.entity.Warehouse;
import com.example.digitallogistics.model.mapper.WarehouseMapper;
import com.example.digitallogistics.service.WarehouseService;
import com.example.digitallogistics.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(WarehouseController.class)
@AutoConfigureMockMvc(addFilters = false)
class WarehouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WarehouseService warehouseService;

    @MockBean
    private WarehouseMapper warehouseMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void list_shouldReturnWarehouses() throws Exception {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(UUID.randomUUID());
        warehouse.setName("Test Warehouse");

        WarehouseDto dto = new WarehouseDto();
        dto.setId(warehouse.getId());
        dto.setName("Test Warehouse");

        when(warehouseService.findAll()).thenReturn(List.of(warehouse));
        when(warehouseMapper.toDto(any(Warehouse.class))).thenReturn(dto);

        mockMvc.perform(get("/api/warehouses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Warehouse"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        WarehouseCreateDto createDto = new WarehouseCreateDto();
        createDto.setName("New Warehouse");
        createDto.setCode("WH001");

        Warehouse warehouse = new Warehouse();
        warehouse.setId(UUID.randomUUID());
        warehouse.setName("New Warehouse");

        WarehouseDto responseDto = new WarehouseDto();
        responseDto.setId(warehouse.getId());
        responseDto.setName("New Warehouse");

        when(warehouseMapper.toEntity(any(WarehouseCreateDto.class))).thenReturn(warehouse);
        when(warehouseService.create(any(Warehouse.class))).thenReturn(warehouse);
        when(warehouseMapper.toDto(any(Warehouse.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/warehouses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Warehouse"));
    }

    @Test
    void get_shouldReturnWarehouse() throws Exception {
        UUID id = UUID.randomUUID();
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setName("Test Warehouse");

        WarehouseDto dto = new WarehouseDto();
        dto.setId(id);
        dto.setName("Test Warehouse");

        when(warehouseService.findById(eq(id))).thenReturn(Optional.of(warehouse));
        when(warehouseMapper.toDto(any(Warehouse.class))).thenReturn(dto);

        mockMvc.perform(get("/api/warehouses/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Warehouse"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);

        when(warehouseService.findById(eq(id))).thenReturn(Optional.of(warehouse));

        mockMvc.perform(delete("/api/warehouses/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        WarehouseDto dto = new WarehouseDto();
        dto.setId(id);
        when(warehouseService.findById(id)).thenReturn(Optional.of(warehouse));
        when(warehouseService.update(eq(id), any())).thenReturn(Optional.of(warehouse));
        when(warehouseMapper.toDto(any())).thenReturn(dto);
        mockMvc.perform(put("/api/warehouses/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void get_shouldReturn404_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(warehouseService.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/warehouses/" + id))
                .andExpect(status().isNotFound());
    }
}
