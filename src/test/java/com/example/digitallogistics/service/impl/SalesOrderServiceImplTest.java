package com.example.digitallogistics.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.digitallogistics.exception.ValidationException;
import com.example.digitallogistics.model.dto.SalesOrderCreateDto;
import com.example.digitallogistics.model.dto.SalesOrderLineCreateDto;
import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.model.entity.SalesOrder;
import com.example.digitallogistics.model.entity.SalesOrderLine;
import com.example.digitallogistics.model.enums.OrderStatus;
import com.example.digitallogistics.repository.ClientRepository;
import com.example.digitallogistics.repository.InventoryRepository;
import com.example.digitallogistics.repository.ProductRepository;
import com.example.digitallogistics.repository.SalesOrderLineRepository;
import com.example.digitallogistics.repository.SalesOrderRepository;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceImplTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private SalesOrderLineRepository salesOrderLineRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private com.example.digitallogistics.model.mapper.UserMapper userMapper;

    @InjectMocks
    private SalesOrderServiceImpl salesOrderService;

    private UUID clientId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }

    @Test
    void create_withSufficientInventory_succeedsAndReserves() {
        SalesOrderCreateDto dto = new SalesOrderCreateDto();
        dto.setClientId(clientId);
        SalesOrderLineCreateDto lineDto = new SalesOrderLineCreateDto();
        lineDto.setProductId(productId);
        lineDto.setQuantity(3);
        dto.setLines(List.of(lineDto));

        Product p = new Product();
        p.setId(productId);
        p.setUnitPrice(BigDecimal.TEN);

        Inventory inv = Inventory.builder().id(UUID.randomUUID()).product(p).qtyOnHand(5).qtyReserved(0).build();

        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of(inv));
        when(productRepository.findById(productId)).thenReturn(Optional.of(p));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(i -> i.getArgument(0));
        when(salesOrderLineRepository.save(any(SalesOrderLine.class))).thenAnswer(i -> i.getArgument(0));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        SalesOrder result = salesOrderService.create(dto);

        assertEquals(OrderStatus.RESERVED, result.getStatus());
    }

    @Test
    void create_withInsufficientInventory_throwsValidationException() {
        SalesOrderCreateDto dto = new SalesOrderCreateDto();
        dto.setClientId(clientId);
        SalesOrderLineCreateDto lineDto = new SalesOrderLineCreateDto();
        lineDto.setProductId(productId);
        lineDto.setQuantity(10);
        dto.setLines(List.of(lineDto));

        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of());

        assertThrows(ValidationException.class, () -> salesOrderService.create(dto));
    }
}
