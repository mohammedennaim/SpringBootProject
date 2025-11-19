package com.example.digitallogistics.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.digitallogistics.model.mapper.UserMapper;
import com.example.digitallogistics.service.AdvancedLogisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.digitallogistics.model.dto.SalesOrderCreateDto;
import com.example.digitallogistics.model.dto.SalesOrderLineCreateDto;
import com.example.digitallogistics.model.entity.*;
import com.example.digitallogistics.model.enums.OrderStatus;
import com.example.digitallogistics.repository.*;

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
    private UserMapper userMapper;
    @Mock
    private AdvancedLogisticsService advancedLogisticsService;

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

        when(productRepository.findById(productId)).thenReturn(Optional.of(p));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(i -> {
            SalesOrder o = i.getArgument(0);
            o.setId(UUID.randomUUID());
            return o;
        });
        when(salesOrderLineRepository.save(any(SalesOrderLine.class))).thenAnswer(i -> i.getArgument(0));
        when(advancedLogisticsService.reserveStock(any(SalesOrder.class))).thenReturn(true);

        SalesOrder result = salesOrderService.create(dto);

        assertNotNull(result);
        assertEquals(OrderStatus.RESERVED, result.getStatus());
        verify(advancedLogisticsService).reserveStock(any(SalesOrder.class));
    }

    @Test
    void create_withInsufficientInventory_createsBackOrder() {
        SalesOrderCreateDto dto = new SalesOrderCreateDto();
        dto.setClientId(clientId);
        SalesOrderLineCreateDto lineDto = new SalesOrderLineCreateDto();
        lineDto.setProductId(productId);
        lineDto.setQuantity(10);
        dto.setLines(List.of(lineDto));

        Product p = new Product();
        p.setId(productId);
        p.setUnitPrice(BigDecimal.TEN);

        when(productRepository.findById(productId)).thenReturn(Optional.of(p));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(i -> {
            SalesOrder o = i.getArgument(0);
            o.setId(UUID.randomUUID());
            return o;
        });
        when(salesOrderLineRepository.save(any(SalesOrderLine.class))).thenAnswer(i -> i.getArgument(0));
        when(advancedLogisticsService.reserveStock(any(SalesOrder.class))).thenReturn(false);

        SalesOrder result = salesOrderService.create(dto);
        
        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getStatus());
    }

    @Test
    void cancel_shouldReleaseReservedInventory() {
        UUID orderId = UUID.randomUUID();
        SalesOrder order = SalesOrder.builder().id(orderId).status(OrderStatus.RESERVED).build();
        
        Product p = new Product();
        p.setId(productId);
        
        SalesOrderLine line = SalesOrderLine.builder()
                .id(UUID.randomUUID())
                .salesOrder(order)
                .product(p)
                .quantity(3)
                .build();

        Inventory inv = Inventory.builder()
                .id(UUID.randomUUID())
                .product(p)
                .qtyOnHand(2)
                .qtyReserved(3)
                .build();

        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(salesOrderLineRepository.findBySalesOrderId(orderId)).thenReturn(List.of(line));
        when(inventoryRepository.findByProductId(productId)).thenReturn(List.of(inv));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(i -> i.getArgument(0));

        SalesOrder result = salesOrderService.cancel(orderId);

        assertEquals(OrderStatus.CANCELED, result.getStatus());
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void findById_shouldReturnOrder() {
        UUID orderId = UUID.randomUUID();
        SalesOrder order = SalesOrder.builder().id(orderId).status(OrderStatus.CREATED).build();
        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Optional<SalesOrder> result = salesOrderService.findById(orderId);
        assertTrue(result.isPresent());
    }

    @Test
    void ship_shouldUpdateStatusToShipped() {
        UUID orderId = UUID.randomUUID();
        SalesOrder order = SalesOrder.builder().id(orderId).status(OrderStatus.RESERVED).build();
        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(salesOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(advancedLogisticsService.canShipOrder(orderId)).thenReturn(true);
        when(advancedLogisticsService.calculateShipmentDate(any())).thenReturn(java.time.LocalDate.now());
        
        SalesOrder result = salesOrderService.ship(orderId);
        assertEquals(OrderStatus.SHIPPED, result.getStatus());
    }

    @Test
    void deliver_shouldUpdateStatusToDelivered() {
        UUID orderId = UUID.randomUUID();
        SalesOrder order = SalesOrder.builder().id(orderId).status(OrderStatus.SHIPPED).build();
        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(salesOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        SalesOrder result = salesOrderService.deliver(orderId);
        assertEquals(OrderStatus.DELIVERED, result.getStatus());
    }

    @Test
    void create_withMultipleLines_shouldReserveAll() {
        SalesOrderCreateDto dto = new SalesOrderCreateDto();
        dto.setClientId(clientId);
        UUID productId2 = UUID.randomUUID();
        SalesOrderLineCreateDto line1 = new SalesOrderLineCreateDto();
        line1.setProductId(productId);
        line1.setQuantity(2);
        SalesOrderLineCreateDto line2 = new SalesOrderLineCreateDto();
        line2.setProductId(productId2);
        line2.setQuantity(1);
        dto.setLines(List.of(line1, line2));

        Product p1 = new Product();
        p1.setId(productId);
        p1.setUnitPrice(BigDecimal.TEN);
        Product p2 = new Product();
        p2.setId(productId2);
        p2.setUnitPrice(BigDecimal.valueOf(20));

        Inventory inv1 = Inventory.builder().id(UUID.randomUUID()).product(p1).qtyOnHand(5).qtyReserved(0).build();
        Inventory inv2 = Inventory.builder().id(UUID.randomUUID()).product(p2).qtyOnHand(3).qtyReserved(0).build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(p1));
        when(productRepository.findById(productId2)).thenReturn(Optional.of(p2));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(i -> {
            SalesOrder o = i.getArgument(0);
            o.setId(UUID.randomUUID());
            return o;
        });
        when(salesOrderLineRepository.save(any(SalesOrderLine.class))).thenAnswer(i -> i.getArgument(0));
        when(advancedLogisticsService.reserveStock(any(SalesOrder.class))).thenReturn(true);

        SalesOrder result = salesOrderService.create(dto);
        assertNotNull(result);
        assertEquals(OrderStatus.RESERVED, result.getStatus());
        verify(advancedLogisticsService).reserveStock(any(SalesOrder.class));
    }

    @Test
    void cancel_shouldNotReleaseIfAlreadyCanceled() {
        UUID orderId = UUID.randomUUID();
        SalesOrder order = SalesOrder.builder().id(orderId).status(OrderStatus.CANCELED).build();
        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(salesOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        SalesOrder result = salesOrderService.cancel(orderId);
        assertEquals(OrderStatus.CANCELED, result.getStatus());
    }
}
