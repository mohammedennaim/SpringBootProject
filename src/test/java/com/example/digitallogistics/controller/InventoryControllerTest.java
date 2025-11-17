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

import com.example.digitallogistics.model.dto.InventoryAdjustDto;
import com.example.digitallogistics.model.dto.InventoryDto;
import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.mapper.InventoryMapper;
import com.example.digitallogistics.service.InventoryService;
import com.example.digitallogistics.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private InventoryMapper inventoryMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void list_shouldReturnAllInventories() throws Exception {
        Inventory inventory = new Inventory();
        inventory.setId(UUID.randomUUID());
        inventory.setQtyOnHand(100);

        InventoryDto dto = new InventoryDto();
        dto.setId(inventory.getId());
        dto.setQtyOnHand(100);

        when(inventoryService.findAll()).thenReturn(List.of(inventory));
        when(inventoryMapper.toDto(any(Inventory.class))).thenReturn(dto);

        mockMvc.perform(get("/api/inventories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].qtyOnHand").value(100));
    }

    @Test
    void get_shouldReturnInventory() throws Exception {
        UUID id = UUID.randomUUID();
        Inventory inventory = new Inventory();
        inventory.setId(id);
        inventory.setQtyOnHand(50);

        InventoryDto dto = new InventoryDto();
        dto.setId(id);
        dto.setQtyOnHand(50);

        when(inventoryService.findById(eq(id))).thenReturn(Optional.of(inventory));
        when(inventoryMapper.toDto(any(Inventory.class))).thenReturn(dto);

        mockMvc.perform(get("/api/inventories/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qtyOnHand").value(50));
    }

    @Test
    void adjustInventory_shouldReturnAdjusted() throws Exception {
        InventoryAdjustDto adjustDto = new InventoryAdjustDto();
        adjustDto.setWarehouseId(UUID.randomUUID());
        adjustDto.setProductId(UUID.randomUUID());
        adjustDto.setAdjustmentQty(10);
        adjustDto.setReason("Restock");

        Inventory adjusted = new Inventory();
        adjusted.setId(UUID.randomUUID());
        adjusted.setQtyOnHand(110);

        InventoryDto dto = new InventoryDto();
        dto.setId(adjusted.getId());
        dto.setQtyOnHand(110);

        when(inventoryService.adjustInventory(any(), any(), any(), any())).thenReturn(adjusted);
        when(inventoryMapper.toDto(any(Inventory.class))).thenReturn(dto);

        mockMvc.perform(post("/api/inventories/adjust")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adjustDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qtyOnHand").value(110));
    }

    @Test
    void getAvailableQuantity_shouldReturnQuantity() throws Exception {
        UUID productId = UUID.randomUUID();
        when(inventoryService.getAvailableQuantity(eq(productId))).thenReturn(75);

        mockMvc.perform(get("/api/inventories/" + productId + "/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(75));
    }

    @Test
    void list_byWarehouse_shouldReturnFiltered() throws Exception {
        UUID warehouseId = UUID.randomUUID();
        Inventory inventory = new Inventory();
        inventory.setId(UUID.randomUUID());
        InventoryDto dto = new InventoryDto();
        when(inventoryService.findByWarehouseId(warehouseId)).thenReturn(List.of(inventory));
        when(inventoryMapper.toDto(any())).thenReturn(dto);
        mockMvc.perform(get("/api/inventories?warehouseId=" + warehouseId))
                .andExpect(status().isOk());
    }

    @Test
    void list_byProduct_shouldReturnFiltered() throws Exception {
        UUID productId = UUID.randomUUID();
        Inventory inventory = new Inventory();
        inventory.setId(UUID.randomUUID());
        InventoryDto dto = new InventoryDto();
        when(inventoryService.findByProductId(productId)).thenReturn(List.of(inventory));
        when(inventoryMapper.toDto(any())).thenReturn(dto);
        mockMvc.perform(get("/api/inventories?productId=" + productId))
                .andExpect(status().isOk());
    }

    @Test
    void updateInventory_shouldReturnUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        Inventory inventory = new Inventory();
        inventory.setId(id);
        InventoryDto dto = new InventoryDto();
        dto.setId(id);
        dto.setWarehouseId(UUID.randomUUID());
        dto.setProductId(UUID.randomUUID());
        dto.setQtyOnHand(100);
        dto.setQtyReserved(10);
        when(inventoryService.findById(id)).thenReturn(Optional.of(inventory));
        when(inventoryService.updateInventory(any(), any(), any(), any(), any())).thenReturn(inventory);
        when(inventoryMapper.toDto(any())).thenReturn(dto);
        mockMvc.perform(put("/api/inventories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getAvailableQuantity_withWarehouse_shouldReturnQuantity() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        when(inventoryService.getAvailableQuantityInWarehouse(warehouseId, productId)).thenReturn(50);
        mockMvc.perform(get("/api/inventories/" + productId + "/available?warehouseId=" + warehouseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(50));
    }
}
