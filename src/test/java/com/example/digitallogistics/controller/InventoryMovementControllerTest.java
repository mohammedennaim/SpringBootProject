package com.example.digitallogistics.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.digitallogistics.model.dto.InventoryMovementCreateDto;
import com.example.digitallogistics.model.entity.InventoryMovement;
import com.example.digitallogistics.model.enums.MovementType;
import com.example.digitallogistics.service.InventoryMovementService;
import com.example.digitallogistics.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(InventoryMovementController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryMovementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryMovementService inventoryMovementService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void list_shouldReturnMovements() throws Exception {
        InventoryMovement movement = new InventoryMovement();
        movement.setId(UUID.randomUUID());
        movement.setType(MovementType.INBOUND);
        movement.setQuantity(100);
        movement.setOccurredAt(LocalDateTime.now());

        when(inventoryMovementService.findAll()).thenReturn(List.of(movement));

        mockMvc.perform(get("/api/inventory-movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(100));
    }

    @Test
    void inbound_shouldRecordMovement() throws Exception {
        InventoryMovementCreateDto createDto = new InventoryMovementCreateDto();
        createDto.setWarehouseId(UUID.randomUUID());
        createDto.setProductId(UUID.randomUUID());
        createDto.setQuantity(50);
        createDto.setReference("REF001");

        InventoryMovement movement = new InventoryMovement();
        movement.setId(UUID.randomUUID());
        movement.setType(MovementType.INBOUND);
        movement.setQuantity(50);
        movement.setOccurredAt(LocalDateTime.now());

        when(inventoryMovementService.recordInbound(any())).thenReturn(movement);

        mockMvc.perform(post("/api/inventory-movements/inbound")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    void outbound_shouldRecordMovement() throws Exception {
        InventoryMovementCreateDto createDto = new InventoryMovementCreateDto();
        createDto.setWarehouseId(UUID.randomUUID());
        createDto.setProductId(UUID.randomUUID());
        createDto.setQuantity(30);

        InventoryMovement movement = new InventoryMovement();
        movement.setId(UUID.randomUUID());
        movement.setType(MovementType.OUTBOUND);
        movement.setQuantity(30);
        movement.setOccurredAt(LocalDateTime.now());

        when(inventoryMovementService.recordOutbound(any())).thenReturn(movement);

        mockMvc.perform(post("/api/inventory-movements/outbound")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(30));
    }

    @Test
    void adjustment_shouldRecordMovement() throws Exception {
        InventoryMovementCreateDto createDto = new InventoryMovementCreateDto();
        createDto.setWarehouseId(UUID.randomUUID());
        createDto.setProductId(UUID.randomUUID());
        createDto.setQuantity(10);
        createDto.setDescription("Adjustment");

        InventoryMovement movement = new InventoryMovement();
        movement.setId(UUID.randomUUID());
        movement.setType(MovementType.ADJUSTMENT);
        movement.setQuantity(10);
        movement.setOccurredAt(LocalDateTime.now());

        when(inventoryMovementService.recordAdjustment(any())).thenReturn(movement);

        mockMvc.perform(post("/api/inventory-movements/adjustment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(10));
    }
}
