package com.example.digitallogistics.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.digitallogistics.model.dto.ShipmentCreateDto;
import com.example.digitallogistics.model.dto.ShipmentDto;
import com.example.digitallogistics.model.dto.ShipmentStatusUpdateDto;
import com.example.digitallogistics.model.enums.ShipmentStatus;
import com.example.digitallogistics.service.ShipmentService;
import com.example.digitallogistics.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ShipmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShipmentService shipmentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void getAllShipments_shouldReturnPage() throws Exception {
        ShipmentDto dto = new ShipmentDto();
        dto.setId(UUID.randomUUID());
        dto.setTrackingNumber("TRACK001");

        Page<ShipmentDto> page = new PageImpl<>(List.of(dto));
        when(shipmentService.getAllShipments(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].trackingNumber").value("TRACK001"));
    }

    @Test
    void createShipment_shouldReturnCreated() throws Exception {
        ShipmentCreateDto createDto = new ShipmentCreateDto();
        createDto.setOrderId(UUID.randomUUID());
        createDto.setCarrierId(UUID.randomUUID());
        createDto.setWarehouseId(UUID.randomUUID());

        ShipmentDto responseDto = new ShipmentDto();
        responseDto.setId(UUID.randomUUID());
        responseDto.setTrackingNumber("TRACK001");

        when(shipmentService.createShipment(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trackingNumber").value("TRACK001"));
    }

    @Test
    void getShipmentById_shouldReturnShipment() throws Exception {
        UUID id = UUID.randomUUID();
        ShipmentDto dto = new ShipmentDto();
        dto.setId(id);
        dto.setTrackingNumber("TRACK001");

        when(shipmentService.getShipmentById(eq(id))).thenReturn(dto);

        mockMvc.perform(get("/api/shipments/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value("TRACK001"));
    }

    @Test
    void updateShipmentStatus_shouldReturnUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        ShipmentStatusUpdateDto statusUpdate = new ShipmentStatusUpdateDto();
        statusUpdate.setStatus(ShipmentStatus.IN_TRANSIT);

        ShipmentDto responseDto = new ShipmentDto();
        responseDto.setId(id);
        responseDto.setStatus(ShipmentStatus.IN_TRANSIT);

        when(shipmentService.updateShipmentStatus(eq(id), any())).thenReturn(responseDto);

        mockMvc.perform(put("/api/shipments/" + id + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_TRANSIT"));
    }

    @Test
    void getShipmentByTrackingNumber_shouldReturnShipment() throws Exception {
        ShipmentDto dto = new ShipmentDto();
        dto.setId(UUID.randomUUID());
        dto.setTrackingNumber("TRACK001");

        when(shipmentService.getShipmentByTrackingNumber(eq("TRACK001"))).thenReturn(dto);

        mockMvc.perform(get("/api/shipments/tracking/TRACK001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value("TRACK001"));
    }
}
