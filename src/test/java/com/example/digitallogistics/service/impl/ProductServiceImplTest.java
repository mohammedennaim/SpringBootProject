package com.example.digitallogistics.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.model.entity.SalesOrderLine;
import com.example.digitallogistics.repository.InventoryRepository;
import com.example.digitallogistics.repository.ProductRepository;
import com.example.digitallogistics.repository.SalesOrderLineRepository;
import com.example.digitallogistics.service.InventoryService;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private SalesOrderLineRepository salesOrderLineRepository;
    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID productId;
    private Product product;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = new Product();
        product.setId(productId);
        product.setSku("TEST-SKU-001");
        product.setName("Test Product");
        product.setCategory("Electronics");
        product.setUnitPrice(BigDecimal.valueOf(99.99));
        product.setActive(true);
    }

    @Test
    void create_shouldSaveProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.create(product);

        assertNotNull(result);
        assertEquals("TEST-SKU-001", result.getSku());
        verify(productRepository).save(product);
    }

    @Test
    void findById_shouldReturnProduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.findById(productId);

        assertTrue(result.isPresent());
        assertEquals(productId, result.get().getId());
        verify(productRepository).findById(productId);
    }

    @Test
    void findBySku_shouldReturnProduct() {
        when(productRepository.findBySku("TEST-SKU-001")).thenReturn(Optional.of(product));

        Optional<Product> result = productService.findBySku("TEST-SKU-001");

        assertTrue(result.isPresent());
        assertEquals("TEST-SKU-001", result.get().getSku());
    }

    @Test
    void update_shouldUpdateExistingProduct() {
        Product updates = new Product();
        updates.setName("Updated Name");
        updates.setCategory("Updated Category");
        updates.setUnitPrice(BigDecimal.valueOf(149.99));
        updates.setActive(false);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Product> result = productService.update(productId, updates);

        assertTrue(result.isPresent());
        assertEquals("Updated Name", result.get().getName());
        assertEquals("Updated Category", result.get().getCategory());
        assertEquals(BigDecimal.valueOf(149.99), result.get().getUnitPrice());
        assertFalse(result.get().isActive());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void deleteById_shouldDeactivateProduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        productService.deleteById(productId);

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void findAll_shouldReturnPagedProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = List.of(product);
        Page<Product> page = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findBySkuContainingIgnoreCaseOrNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                anyString(), anyString(), anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<Product> result = productService.findAll(pageable, "test", null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("TEST-SKU-001", result.getContent().get(0).getSku());
    }

    @Test
    void desactivate_shouldReturnTrueWhenNoReservedInventory() {
        when(productRepository.findBySku("TEST-SKU-001")).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of());
        when(salesOrderLineRepository.findByProductId(productId)).thenReturn(List.of());

        boolean result = productService.desactivate("TEST-SKU-001");

        assertTrue(result);
    }

    @Test
    void desactivate_shouldReturnFalseWhenProductNotFound() {
        when(productRepository.findBySku("UNKNOWN")).thenReturn(Optional.empty());

        boolean result = productService.desactivate("UNKNOWN");

        assertFalse(result);
    }
}
