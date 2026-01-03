package com.example.digitallogistics.controller;

import static org.mockito.ArgumentMatchers.any;
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

import com.example.digitallogistics.model.dto.SalesOrderCreateDto;
import com.example.digitallogistics.model.dto.SalesOrderLineCreateDto;
import com.example.digitallogistics.model.entity.SalesOrder;
import com.example.digitallogistics.model.mapper.ClientMapper;
import com.example.digitallogistics.security.CustomUserDetailsService;
import com.example.digitallogistics.service.SalesOrderService;
import com.example.digitallogistics.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(SalesOrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class SalesOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SalesOrderService salesOrderService;

    @MockBean
    private ClientMapper clientMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void list_shouldReturnOrders() throws Exception {
        SalesOrder order = new SalesOrder();
        order.setId(UUID.randomUUID());

        when(salesOrderService.findAll(any(), any())).thenReturn(List.of(order));
        when(salesOrderService.findLines(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/sales-orders"))
                .andExpect(status().isOk());
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        SalesOrderLineCreateDto lineDto = new SalesOrderLineCreateDto();
        lineDto.setProductId(UUID.randomUUID());
        lineDto.setQuantity(5);
        
        SalesOrderCreateDto createDto = new SalesOrderCreateDto();
        createDto.setClientId(UUID.randomUUID());
        createDto.setLines(List.of(lineDto));

        SalesOrder order = new SalesOrder();
        order.setId(UUID.randomUUID());

        when(salesOrderService.create(any())).thenReturn(order);
        when(salesOrderService.findLines(any())).thenReturn(List.of());

        mockMvc.perform(post("/api/sales-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }

    @Test
    void get_shouldReturnOrder() throws Exception {
        UUID id = UUID.randomUUID();
        SalesOrder order = new SalesOrder();
        order.setId(id);

        when(salesOrderService.findById(id)).thenReturn(Optional.of(order));
        when(salesOrderService.findLines(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/sales-orders/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void reserve_shouldReturnUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        SalesOrder order = new SalesOrder();
        order.setId(id);

        when(salesOrderService.reserve(id)).thenReturn(order);
        when(salesOrderService.findLines(any())).thenReturn(List.of());

        mockMvc.perform(put("/api/sales-orders/" + id + "/reserve"))
                .andExpect(status().isOk());
    }

    @Test
    void cancel_shouldReturnUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        SalesOrder order = new SalesOrder();
        order.setId(id);

        when(salesOrderService.cancel(id)).thenReturn(order);
        when(salesOrderService.findLines(any())).thenReturn(List.of());

        mockMvc.perform(put("/api/sales-orders/" + id + "/cancel"))
                .andExpect(status().isOk());
    }

    @Test
    void ship_shouldReturnUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        SalesOrder order = new SalesOrder();
        order.setId(id);
        when(salesOrderService.ship(id)).thenReturn(order);
        when(salesOrderService.findLines(any())).thenReturn(List.of());
        mockMvc.perform(put("/api/sales-orders/" + id + "/ship"))
                .andExpect(status().isOk());
    }

    @Test
    void deliver_shouldReturnUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        SalesOrder order = new SalesOrder();
        order.setId(id);
        when(salesOrderService.deliver(id)).thenReturn(order);
        when(salesOrderService.findLines(any())).thenReturn(List.of());
        mockMvc.perform(put("/api/sales-orders/" + id + "/deliver"))
                .andExpect(status().isOk());
    }

    @Test
    void get_shouldReturn404_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(salesOrderService.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/sales-orders/" + id))
                .andExpect(status().isNotFound());
    }
}
