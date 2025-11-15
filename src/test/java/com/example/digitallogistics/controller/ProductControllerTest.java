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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.digitallogistics.model.dto.ProductCreateDto;
import com.example.digitallogistics.model.dto.ProductDto;
import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.model.mapper.ProductMapper;
import com.example.digitallogistics.service.ProductService;
import com.example.digitallogistics.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductMapper productMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void list_shouldReturnPage() throws Exception {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Test Product");

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName("Test Product");

        Page<Product> page = new PageImpl<>(List.of(product));
        when(productService.findAll(any(Pageable.class), any(), any())).thenReturn(page);
        when(productMapper.toDto(any(Product.class))).thenReturn(dto);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        ProductCreateDto createDto = new ProductCreateDto();
        createDto.setName("New Product");
        createDto.setSku("SKU001");
        createDto.setUnitPrice(new java.math.BigDecimal("99.99"));

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("New Product");

        ProductDto responseDto = new ProductDto();
        responseDto.setId(product.getId());
        responseDto.setName("New Product");

        when(productMapper.toEntity(any(ProductCreateDto.class))).thenReturn(product);
        when(productService.create(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Product"));
    }

    @Test
    void get_shouldReturnProduct() throws Exception {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);
        product.setName("Test Product");

        ProductDto dto = new ProductDto();
        dto.setId(id);
        dto.setName("Test Product");

        when(productService.findById(eq(id))).thenReturn(Optional.of(product));
        when(productMapper.toDto(any(Product.class))).thenReturn(dto);

        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void searchBySku_shouldReturnProduct() throws Exception {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setSku("SKU001");

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setSku("SKU001");

        when(productService.findBySku(eq("SKU001"))).thenReturn(Optional.of(product));
        when(productMapper.toDto(any(Product.class))).thenReturn(dto);

        mockMvc.perform(get("/api/products/search?sku=SKU001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU001"));
    }
}
