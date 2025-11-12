package com.example.digitallogistics.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.digitallogistics.model.dto.PurchaseOrderCreateDto;
import com.example.digitallogistics.model.dto.PurchaseOrderReceiveDto;
import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.model.entity.PurchaseOrder;
import com.example.digitallogistics.model.entity.PurchaseOrderLine;
import com.example.digitallogistics.model.enums.PurchaseOrderStatus;
import com.example.digitallogistics.repository.InventoryRepository;
import com.example.digitallogistics.repository.ProductRepository;
import com.example.digitallogistics.repository.PurchaseOrderLineRepository;
import com.example.digitallogistics.repository.PurchaseOrderRepository;
import com.example.digitallogistics.repository.SupplierRepository;
import com.example.digitallogistics.service.PurchaseOrderService;
import com.example.digitallogistics.repository.WarehouseRepository;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
            PurchaseOrderLineRepository purchaseOrderLineRepository, SupplierRepository supplierRepository,
            ProductRepository productRepository, InventoryRepository inventoryRepository,
            WarehouseRepository warehouseRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderLineRepository = purchaseOrderLineRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public List<PurchaseOrder> findAll(Optional<UUID> supplierId, Optional<PurchaseOrderStatus> status) {
        if (supplierId.isPresent()) return purchaseOrderRepository.findBySupplierId(supplierId.get());
        if (status.isPresent()) return purchaseOrderRepository.findByStatus(status.get());
        return purchaseOrderRepository.findAll();
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public PurchaseOrder create(PurchaseOrderCreateDto dto) {
        PurchaseOrder po = PurchaseOrder.builder()
                .status(PurchaseOrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
        .expectedDelivery(dto.getExpectedDelivery())
                .build();

    supplierRepository.findById(dto.getSupplierId()).ifPresent(po::setSupplier);

        PurchaseOrder saved = purchaseOrderRepository.save(po);

        List<PurchaseOrderLine> lines = new ArrayList<>();
    for (var l : dto.getLines()) {
        Product p = productRepository.findById(l.getProductId()).orElse(null);
            PurchaseOrderLine line = PurchaseOrderLine.builder()
                    .product(p)
                    .purchaseOrder(saved)
            .quantity(l.getQuantity())
            .unitPrice(l.getUnitPrice())
                    .build();
            lines.add(purchaseOrderLineRepository.save(line));
        }

        saved.setCreatedAt(LocalDateTime.now());
        return purchaseOrderRepository.save(saved);
    }

    @Override
    @SuppressWarnings("null")
    public Optional<PurchaseOrder> findById(UUID id) {
        return purchaseOrderRepository.findById(id);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public PurchaseOrder approve(UUID id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("PO not found"));
        if (po.getStatus() != PurchaseOrderStatus.CREATED) {
            throw new RuntimeException("Only CREATED POs can be approved");
        }
        po.setStatus(PurchaseOrderStatus.APPROVED);
        return purchaseOrderRepository.save(po);
    }


    
    @SuppressWarnings("null")
    @Override
    @Transactional
    public PurchaseOrder receive(UUID id, PurchaseOrderReceiveDto dto, UUID warehouseId) {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("PO not found"));
        if (po.getStatus() != PurchaseOrderStatus.APPROVED && po.getStatus() != PurchaseOrderStatus.CREATED) {
            throw new RuntimeException("Only CREATED or APPROVED POs can be received");
        }

        for (var rl : dto.getLines()) {
            var poline = purchaseOrderLineRepository.findById(rl.getLineId()).orElseThrow(() -> new RuntimeException("PO line not found"));
            int toReceive = rl.getReceivedQuantity() != null ? rl.getReceivedQuantity() : 0;

            UUID productId = poline.getProduct() != null ? poline.getProduct().getId() : null;
            Inventory inventory = null;
            if (productId != null) {
                inventory = inventoryRepository.findByWarehouseIdAndProductId(warehouseId, productId).orElse(null);
            }

            if (inventory == null) {
                var wh = warehouseRepository.findById(warehouseId)
                        .orElseThrow(() -> new RuntimeException("Warehouse not found"));
                inventory = Inventory.builder()
                        .warehouse(wh)
                        .product(poline.getProduct())
                        .qtyOnHand(0)
                        .qtyReserved(0)
                        .build();
                inventory = inventoryRepository.save(inventory);
            }

            inventory.setQtyOnHand((inventory.getQtyOnHand() != null ? inventory.getQtyOnHand() : 0) + toReceive);
            inventoryRepository.save(inventory);
        }

        po.setStatus(PurchaseOrderStatus.RECEIVED);
        return purchaseOrderRepository.save(po);
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public PurchaseOrder cancel(UUID id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("PO not found"));
        po.setStatus(PurchaseOrderStatus.CANCELED);
        return purchaseOrderRepository.save(po);
    }
}
