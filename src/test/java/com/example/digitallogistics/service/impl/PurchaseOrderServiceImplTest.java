package com.example.digitallogistics.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.digitallogistics.model.dto.PurchaseOrderReceiveDto;
import com.example.digitallogistics.model.dto.PurchaseOrderReceiveLineDto;
import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.model.entity.PurchaseOrder;
import com.example.digitallogistics.model.entity.PurchaseOrderLine;
import com.example.digitallogistics.model.entity.Warehouse;
import com.example.digitallogistics.model.enums.PurchaseOrderStatus;
import com.example.digitallogistics.repository.InventoryRepository;
import com.example.digitallogistics.repository.ProductRepository;
import com.example.digitallogistics.repository.PurchaseOrderLineRepository;
import com.example.digitallogistics.repository.PurchaseOrderRepository;
import com.example.digitallogistics.repository.SupplierRepository;
import com.example.digitallogistics.repository.WarehouseRepository;
import com.example.digitallogistics.service.PurchaseOrderService;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceImplTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseOrderLineRepository purchaseOrderLineRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private PurchaseOrderServiceImpl purchaseOrderService;

    private UUID poId;
    private UUID lineId;
    private UUID warehouseId;

    @BeforeEach
    void setUp() {
        poId = UUID.randomUUID();
        lineId = UUID.randomUUID();
        warehouseId = UUID.randomUUID();
    }

    @Test
    void whenInventoryExists_receive_updatesQtyAndStatus() {
        PurchaseOrder po = PurchaseOrder.builder()
                .id(poId)
                .status(PurchaseOrderStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();

        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        PurchaseOrderLine pol = PurchaseOrderLine.builder()
                .id(lineId)
                .product(product1)
                .quantity(5)
                .unitPrice(BigDecimal.TEN)
                .purchaseOrder(po)
                .build();

        Inventory inv = Inventory.builder()
                .id(UUID.randomUUID())
                .product(pol.getProduct())
                .warehouse(Warehouse.builder().id(warehouseId).build())
                .qtyOnHand(2)
                .qtyReserved(0)
                .build();

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(po));
        when(purchaseOrderLineRepository.findById(lineId)).thenReturn(Optional.of(pol));
        when(inventoryRepository.findByWarehouseIdAndProductId(warehouseId, pol.getProduct().getId())).thenReturn(Optional.of(inv));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(i -> i.getArgument(0));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        PurchaseOrderReceiveLineDto rl = PurchaseOrderReceiveLineDto.builder()
                .lineId(lineId)
                .receivedQuantity(3)
                .build();
        PurchaseOrderReceiveDto dto = PurchaseOrderReceiveDto.builder().lines(List.of(rl)).build();
        PurchaseOrder result = purchaseOrderService.receive(poId, dto, warehouseId);
        assertNotNull(result);
        assertEquals(PurchaseOrderStatus.RECEIVED, result.getStatus());
        assertEquals(5, inventoryRepository.findByWarehouseIdAndProductId(warehouseId, pol.getProduct().getId()).orElse(inv).getQtyOnHand() );
    }

    @Test
    void whenInventoryMissing_receive_createsInventoryAndUpdatesQty() {
        PurchaseOrder po = PurchaseOrder.builder()
                .id(poId)
                .status(PurchaseOrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        Product product = new Product();
        product.setId(UUID.randomUUID());
        PurchaseOrderLine pol = PurchaseOrderLine.builder()
                .id(lineId)
                .product(product)
                .quantity(2)
                .unitPrice(BigDecimal.ONE)
                .purchaseOrder(po)
                .build();

        Warehouse wh = Warehouse.builder().id(warehouseId).build();
        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(po));
        when(purchaseOrderLineRepository.findById(lineId)).thenReturn(Optional.of(pol));
        when(inventoryRepository.findByWarehouseIdAndProductId(warehouseId, product.getId())).thenReturn(Optional.empty());
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(wh));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(i -> i.getArgument(0));

        PurchaseOrderReceiveLineDto rl = PurchaseOrderReceiveLineDto.builder()
                .lineId(lineId)
                .receivedQuantity(4)
                .build();
        PurchaseOrderReceiveDto dto = PurchaseOrderReceiveDto.builder().lines(List.of(rl)).build();
        PurchaseOrder result = purchaseOrderService.receive(poId, dto, warehouseId);
        assertNotNull(result);
        assertEquals(PurchaseOrderStatus.RECEIVED, result.getStatus());
    }
}
