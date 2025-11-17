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

import com.example.digitallogistics.model.dto.CarrierCreateDto;
import com.example.digitallogistics.model.dto.CarrierDto;
import com.example.digitallogistics.model.enums.CarrierStatus;
import com.example.digitallogistics.service.CarrierService;
import com.example.digitallogistics.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CarrierController.class)
@AutoConfigureMockMvc(addFilters = false)
class CarrierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarrierService carrierService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void getAllCarriers_shouldReturnPage() throws Exception {
        CarrierDto dto = new CarrierDto();
        dto.setId(UUID.randomUUID());
        dto.setName("Test Carrier");
        dto.setCode("TC001");

        Page<CarrierDto> page = new PageImpl<>(List.of(dto));
        when(carrierService.getAllCarriers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/carriers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Carrier"));
    }

    @Test
    void createCarrier_shouldReturnCreated() throws Exception {
        CarrierCreateDto createDto = new CarrierCreateDto();
        createDto.setName("New Carrier");
        createDto.setCode("NC001");
        createDto.setPhone("+1234567890");
        createDto.setMaxDailyShipments(100);

        CarrierDto responseDto = new CarrierDto();
        responseDto.setId(UUID.randomUUID());
        responseDto.setName("New Carrier");
        responseDto.setCode("NC001");

        when(carrierService.createCarrier(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/carriers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Carrier"));
    }

    @Test
    void getCarrierById_shouldReturnCarrier() throws Exception {
        UUID id = UUID.randomUUID();
        CarrierDto dto = new CarrierDto();
        dto.setId(id);
        dto.setName("Test Carrier");

        when(carrierService.getCarrierById(eq(id))).thenReturn(dto);

        mockMvc.perform(get("/api/carriers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Carrier"));
    }

    @Test
    void getAllActiveCarriers_shouldReturnList() throws Exception {
        CarrierDto dto = new CarrierDto();
        dto.setId(UUID.randomUUID());
        dto.setName("Active Carrier");
        dto.setStatus(CarrierStatus.ACTIVE);

        when(carrierService.getAllActiveCarriers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/carriers/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Active Carrier"));
    }

    @Test
    void updateCarrier_shouldReturnUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        CarrierDto dto = new CarrierDto();
        dto.setId(id);
        when(carrierService.updateCarrier(eq(id), any())).thenReturn(dto);
        mockMvc.perform(put("/api/carriers/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\",\"phone\":\"+123\",\"maxDailyShipments\":100}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateCarrierStatus_shouldReturnUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        CarrierDto dto = new CarrierDto();
        dto.setId(id);
        when(carrierService.updateCarrierStatus(eq(id), any())).thenReturn(dto);
        mockMvc.perform(patch("/api/carriers/" + id + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCarrier_shouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/api/carriers/" + id))
                .andExpect(status().isNoContent());
    }
}
