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

import com.example.digitallogistics.model.dto.SupplierCreateDto;
import com.example.digitallogistics.model.dto.SupplierDto;
import com.example.digitallogistics.model.entity.Supplier;
import com.example.digitallogistics.model.mapper.SupplierMapper;
import com.example.digitallogistics.service.SupplierService;
import com.example.digitallogistics.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(SupplierController.class)
@AutoConfigureMockMvc(addFilters = false)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SupplierService supplierService;

    @MockBean
    private SupplierMapper supplierMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void list_shouldReturnSuppliers() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setId(UUID.randomUUID());
        supplier.setName("Test Supplier");

        SupplierDto dto = new SupplierDto();
        dto.setId(supplier.getId());
        dto.setName("Test Supplier");

        when(supplierService.findAll()).thenReturn(List.of(supplier));
        when(supplierMapper.toDto(any(Supplier.class))).thenReturn(dto);

        mockMvc.perform(get("/api/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Supplier"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        SupplierCreateDto createDto = new SupplierCreateDto();
        createDto.setName("New Supplier");
        createDto.setContact("+1234567890");

        Supplier supplier = new Supplier();
        supplier.setId(UUID.randomUUID());
        supplier.setName("New Supplier");

        SupplierDto responseDto = new SupplierDto();
        responseDto.setId(supplier.getId());
        responseDto.setName("New Supplier");

        when(supplierMapper.toEntity(any(SupplierCreateDto.class))).thenReturn(supplier);
        when(supplierService.create(any(Supplier.class))).thenReturn(supplier);
        when(supplierMapper.toDto(any(Supplier.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Supplier"));
    }

    @Test
    void get_shouldReturnSupplier() throws Exception {
        UUID id = UUID.randomUUID();
        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier.setName("Test Supplier");

        SupplierDto dto = new SupplierDto();
        dto.setId(id);
        dto.setName("Test Supplier");

        when(supplierService.findById(eq(id))).thenReturn(Optional.of(supplier));
        when(supplierMapper.toDto(any(Supplier.class))).thenReturn(dto);

        mockMvc.perform(get("/api/suppliers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Supplier"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        Supplier supplier = new Supplier();
        supplier.setId(id);

        when(supplierService.findById(eq(id))).thenReturn(Optional.of(supplier));

        mockMvc.perform(delete("/api/suppliers/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        Supplier supplier = new Supplier();
        supplier.setId(id);
        SupplierDto dto = new SupplierDto();
        dto.setId(id);
        when(supplierService.findById(id)).thenReturn(Optional.of(supplier));
        when(supplierService.update(eq(id), any())).thenReturn(Optional.of(supplier));
        when(supplierMapper.toDto(any())).thenReturn(dto);
        mockMvc.perform(put("/api/suppliers/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void list_withSearch_shouldReturnFiltered() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setId(UUID.randomUUID());
        SupplierDto dto = new SupplierDto();
        when(supplierService.findByNameContaining("test")).thenReturn(List.of(supplier));
        when(supplierMapper.toDto(any())).thenReturn(dto);
        mockMvc.perform(get("/api/suppliers?search=test"))
                .andExpect(status().isOk());
    }
}
