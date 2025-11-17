package com.example.digitallogistics.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.digitallogistics.model.dto.PurchaseOrderCreateDto;
import com.example.digitallogistics.model.dto.PurchaseOrderLineCreateDto;
import com.example.digitallogistics.model.dto.PurchaseOrderReceiveDto;
import com.example.digitallogistics.model.dto.PurchaseOrderReceiveLineDto;
import com.example.digitallogistics.model.entity.*;
import com.example.digitallogistics.model.enums.PurchaseOrderStatus;
import com.example.digitallogistics.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceImplTest {

    @Mock
    PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    PurchaseOrderLineRepository purchaseOrderLineRepository;
    @Mock
    SupplierRepository supplierRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    InventoryRepository inventoryRepository;
    @Mock
    WarehouseRepository warehouseRepository;

    @InjectMocks
    PurchaseOrderServiceImpl service;

    UUID productId;
    UUID supplierId;
    UUID warehouseId;
    UUID poId;
    UUID lineId;

    @BeforeEach
    void setup() {
        productId = UUID.randomUUID();
        supplierId = UUID.randomUUID();
        warehouseId = UUID.randomUUID();
        poId = UUID.randomUUID();
        lineId = UUID.randomUUID();
    }

    @Test
    void create_shouldSavePurchaseOrderAndLines() {
        PurchaseOrderCreateDto dto = new PurchaseOrderCreateDto();
        PurchaseOrderLineCreateDto line = new PurchaseOrderLineCreateDto();
        line.setProductId(productId);
        line.setQuantity(5);
        line.setUnitPrice(BigDecimal.valueOf(10.0));
        dto.setLines(List.of(line));
        dto.setSupplierId(supplierId);

        Supplier sup = Supplier.builder().id(supplierId).name("S").build();
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(sup));

        Product p = new Product();
        p.setId(productId);
        p.setSku("SKU");
        p.setUnitPrice(BigDecimal.valueOf(10.0));
        when(productRepository.findById(productId)).thenReturn(Optional.of(p));

        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(i -> {
            PurchaseOrder po = i.getArgument(0);
            po.setId(UUID.randomUUID());
            return po;
        });

        when(purchaseOrderLineRepository.save(any(PurchaseOrderLine.class))).thenAnswer(i -> {
            PurchaseOrderLine l = i.getArgument(0);
            l.setId(UUID.randomUUID());
            return l;
        });

        PurchaseOrder saved = service.create(dto);

        assertNotNull(saved);
        assertEquals(PurchaseOrderStatus.CREATED, saved.getStatus());
        verify(purchaseOrderRepository, atLeastOnce()).save(any(PurchaseOrder.class));
        verify(purchaseOrderLineRepository, times(1)).save(any(PurchaseOrderLine.class));
    }

    @Test
    void approve_shouldSetStatusApproved() {
        PurchaseOrder po = PurchaseOrder.builder().id(poId).status(PurchaseOrderStatus.CREATED).build();
        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(po));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder res = service.approve(poId);
        assertEquals(PurchaseOrderStatus.APPROVED, res.getStatus());
    }

    @Test
    void receive_shouldCreateInventoryWhenMissingAndUpdateQty() {
        Product prod = new Product();
        prod.setId(productId);
        PurchaseOrderLine pol = PurchaseOrderLine.builder().id(lineId).product(prod).quantity(5).build();
        PurchaseOrder po = PurchaseOrder.builder().id(poId).status(PurchaseOrderStatus.APPROVED).build();

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(po));
        when(purchaseOrderLineRepository.findById(lineId)).thenReturn(Optional.of(pol));
        when(inventoryRepository.findByWarehouseIdAndProductId(warehouseId, productId)).thenReturn(Optional.empty());

        Warehouse wh = Warehouse.builder().id(warehouseId).code("W").build();
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(wh));

        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrderReceiveLineDto lr = PurchaseOrderReceiveLineDto.builder()
                .lineId(lineId)
                .receivedQuantity(3)
                .build();
        PurchaseOrderReceiveDto dto = PurchaseOrderReceiveDto.builder().lines(List.of(lr)).build();

        PurchaseOrder res = service.receive(poId, dto, warehouseId);

        assertEquals(PurchaseOrderStatus.RECEIVED, res.getStatus());
        verify(inventoryRepository, atLeastOnce()).save(any(Inventory.class));
    }

    @Test
    void cancel_shouldSetStatusCanceled() {
        PurchaseOrder po = PurchaseOrder.builder().id(poId).status(PurchaseOrderStatus.CREATED).build();
        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(po));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder res = service.cancel(poId);

        assertEquals(PurchaseOrderStatus.CANCELED, res.getStatus());
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
    }

    @Test
    void findById_shouldReturnOrder() {
        PurchaseOrder po = PurchaseOrder.builder().id(poId).build();
        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(po));
        Optional<PurchaseOrder> result = service.findById(poId);
        assertTrue(result.isPresent());
    }

    @Test
    void receive_shouldUpdateExistingInventory() {
        Product prod = new Product();
        prod.setId(productId);
        PurchaseOrderLine pol = PurchaseOrderLine.builder().id(lineId).product(prod).quantity(5).build();
        PurchaseOrder po = PurchaseOrder.builder().id(poId).status(PurchaseOrderStatus.APPROVED).build();
        Inventory inv = Inventory.builder().id(UUID.randomUUID()).qtyOnHand(10).build();

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(po));
        when(purchaseOrderLineRepository.findById(lineId)).thenReturn(Optional.of(pol));
        when(inventoryRepository.findByWarehouseIdAndProductId(warehouseId, productId)).thenReturn(Optional.of(inv));
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrderReceiveLineDto lr = PurchaseOrderReceiveLineDto.builder()
                .lineId(lineId)
                .receivedQuantity(3)
                .build();
        PurchaseOrderReceiveDto dto = PurchaseOrderReceiveDto.builder().lines(List.of(lr)).build();

        PurchaseOrder res = service.receive(poId, dto, warehouseId);
        assertEquals(PurchaseOrderStatus.RECEIVED, res.getStatus());
    }
}
