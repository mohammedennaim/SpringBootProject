package com.example.digitallogistics.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.example.digitallogistics.model.dto.PurchaseOrderCreateDto;
import com.example.digitallogistics.model.dto.PurchaseOrderReceiveDto;
import com.example.digitallogistics.model.entity.PurchaseOrder;
import com.example.digitallogistics.model.mapper.SupplierMapper;
import com.example.digitallogistics.repository.PurchaseOrderLineRepository;
import com.example.digitallogistics.service.PurchaseOrderService;
import com.example.digitallogistics.util.JwtTokenProvider;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PurchaseOrderController.class)
public class PurchaseOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseOrderService purchaseOrderService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private SupplierMapper supplierMapper;

    @MockBean
    private PurchaseOrderLineRepository purchaseOrderLineRepository;

    @Test
    void create_returnsCreatedPoDto() throws Exception {
        PurchaseOrderCreateDto dto = new PurchaseOrderCreateDto();
        dto.setExpectedDelivery(null);

        PurchaseOrder created = new PurchaseOrder();
        created.setId(UUID.randomUUID());

        Mockito.when(purchaseOrderService.create(Mockito.any(PurchaseOrderCreateDto.class))).thenReturn(created);
        Mockito.when(purchaseOrderLineRepository.findByPurchaseOrderId(created.getId())).thenReturn(java.util.List.of());

        mockMvc.perform(post("/api/purchase-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId().toString()));
    }

    @Test
    void receive_returnsUpdatedPoDto() throws Exception {
        UUID poId = UUID.randomUUID();
        UUID whId = UUID.randomUUID();

        PurchaseOrderReceiveDto dto = new PurchaseOrderReceiveDto();
        dto.setLines(java.util.List.of());

        PurchaseOrder updated = new PurchaseOrder();
        updated.setId(poId);

        Mockito.when(purchaseOrderService.receive(Mockito.eq(poId), Mockito.any(PurchaseOrderReceiveDto.class), Mockito.eq(whId)))
                .thenReturn(updated);
        Mockito.when(purchaseOrderLineRepository.findByPurchaseOrderId(poId)).thenReturn(java.util.List.of());

        mockMvc.perform(put("/api/purchase-orders/" + poId + "/receive")
                .param("warehouseId", whId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(poId.toString()));
    }
}
