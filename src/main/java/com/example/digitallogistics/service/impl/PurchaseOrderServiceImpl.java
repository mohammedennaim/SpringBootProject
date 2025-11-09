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

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
            PurchaseOrderLineRepository purchaseOrderLineRepository, SupplierRepository supplierRepository,
            ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderLineRepository = purchaseOrderLineRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<PurchaseOrder> findAll(Optional<UUID> supplierId, Optional<PurchaseOrderStatus> status) {
        if (supplierId.isPresent()) return purchaseOrderRepository.findBySupplierId(supplierId.get());
        if (status.isPresent()) return purchaseOrderRepository.findByStatus(status.get());
        return purchaseOrderRepository.findAll();
    }

    @Override
    @Transactional
    public PurchaseOrder create(PurchaseOrderCreateDto dto) {
        PurchaseOrder po = PurchaseOrder.builder()
                .status(PurchaseOrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .expectedDelivery(dto.expectedDelivery)
                .build();

        supplierRepository.findById(dto.supplierId).ifPresent(po::setSupplier);

        PurchaseOrder saved = purchaseOrderRepository.save(po);

        List<PurchaseOrderLine> lines = new ArrayList<>();
        for (var l : dto.lines) {
            Product p = productRepository.findById(l.productId).orElse(null);
            PurchaseOrderLine line = PurchaseOrderLine.builder()
                    .product(p)
                    .purchaseOrder(saved)
                    .quantity(l.quantity)
                    .unitPrice(l.unitPrice)
                    .build();
            lines.add(purchaseOrderLineRepository.save(line));
        }

        saved.setCreatedAt(LocalDateTime.now());
        return purchaseOrderRepository.save(saved);
    }

    @Override
    public Optional<PurchaseOrder> findById(UUID id) {
        return purchaseOrderRepository.findById(id);
    }

    @Override
    @Transactional
    public PurchaseOrder approve(UUID id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("PO not found"));
        if (po.getStatus() != PurchaseOrderStatus.CREATED) {
            throw new RuntimeException("Only CREATED POs can be approved");
        }
        po.setStatus(PurchaseOrderStatus.APPROVED);
        return purchaseOrderRepository.save(po);
    }

    @Override
    @Transactional
    public PurchaseOrder receive(UUID id, PurchaseOrderReceiveDto dto) {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("PO not found"));
        if (po.getStatus() != PurchaseOrderStatus.APPROVED && po.getStatus() != PurchaseOrderStatus.CREATED) {
            throw new RuntimeException("Only CREATED or APPROVED POs can be received");
        }

        int totalRequested = 0;
        int totalReceived = 0;

        for (var rl : dto.lines) {
            var poline = purchaseOrderLineRepository.findById(rl.lineId).orElseThrow(() -> new RuntimeException("PO line not found"));
            int toReceive = rl.receivedQuantity != null ? rl.receivedQuantity : 0;
            totalRequested += poline.getQuantity() != null ? poline.getQuantity() : 0;
            totalReceived += toReceive;

            // update inventory: pick first inventory row for product and add to qtyOnHand
            List<Inventory> inventories = inventoryRepository.findByProductId(poline.getProduct().getId());
            if (inventories.isEmpty()) {
                // create a new inventory record? For simplicity, skip creating new and continue
                continue;
            }
            Inventory inv = inventories.get(0);
            inv.setQtyOnHand((inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0) + toReceive);
            inventoryRepository.save(inv);
        }

        po.setStatus(PurchaseOrderStatus.RECEIVED);
        return purchaseOrderRepository.save(po);
    }

    @Override
    @Transactional
    public PurchaseOrder cancel(UUID id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("PO not found"));
        po.setStatus(PurchaseOrderStatus.CANCELED);
        return purchaseOrderRepository.save(po);
    }
}
