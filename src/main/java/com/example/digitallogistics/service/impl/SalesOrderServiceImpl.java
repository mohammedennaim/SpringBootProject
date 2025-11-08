package com.example.digitallogistics.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.digitallogistics.model.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.digitallogistics.model.dto.SalesOrderCreateDto;
import com.example.digitallogistics.model.dto.SalesOrderLineCreateDto;
import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.model.entity.SalesOrder;
import com.example.digitallogistics.model.entity.SalesOrderLine;
import com.example.digitallogistics.model.enums.OrderStatus;
import com.example.digitallogistics.repository.InventoryRepository;
import com.example.digitallogistics.repository.ProductRepository;
import com.example.digitallogistics.repository.SalesOrderLineRepository;
import com.example.digitallogistics.repository.SalesOrderRepository;
import com.example.digitallogistics.service.SalesOrderService;
import com.example.digitallogistics.repository.ClientRepository;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderLineRepository salesOrderLineRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final com.example.digitallogistics.repository.ClientRepository clientRepository;
    private final UserMapper userMapper;

    public SalesOrderServiceImpl(SalesOrderRepository salesOrderRepository,
            SalesOrderLineRepository salesOrderLineRepository, InventoryRepository inventoryRepository,
            ProductRepository productRepository, ClientRepository clientRepository,
                                 UserMapper userMapper) {
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderLineRepository = salesOrderLineRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<SalesOrder> findAll(Optional<UUID> clientId, Optional<OrderStatus> status) {
        if (clientId.isPresent()) {
            return salesOrderRepository.findByClientId(clientId.get());
        }
        if (status.isPresent()) {
            return salesOrderRepository.findByStatus(status.get());
        }
        return salesOrderRepository.findAll();
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public SalesOrder create(SalesOrderCreateDto dto) {
    SalesOrder order = SalesOrder.builder()
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

    clientRepository.findById(dto.clientId).ifPresent(order::setClient);

        SalesOrder saved = salesOrderRepository.save(order);

        List<SalesOrderLine> lines = new ArrayList<>();
        for (SalesOrderLineCreateDto l : dto.lines) {
            @SuppressWarnings("null")
            Product p = productRepository.findById(l.productId).orElse(null);
            SalesOrderLine line = SalesOrderLine.builder()
                    .product(p)
                    .quantity(l.quantity)
                    .unitPrice(p != null ? p.getUnitPrice() : null)
                    .salesOrder(saved)
                    .backorder(false)
                    .build();
            lines.add(salesOrderLineRepository.save(line));
        }

        saved.setCreatedAt(LocalDateTime.now());
        return salesOrderRepository.save(saved);
    }

    @SuppressWarnings("null")
    @Override
    public Optional<SalesOrder> findById(UUID id) {
        return salesOrderRepository.findById(id);
    }

    @Override
    public List<com.example.digitallogistics.model.entity.SalesOrderLine> findLines(UUID orderId) {
        return salesOrderLineRepository.findBySalesOrderId(orderId);
    }

    @Override
    @Transactional
    public SalesOrder reserve(UUID id) {
        @SuppressWarnings("null")
        SalesOrder order = salesOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Only CREATED orders can be reserved");
        }

        List<SalesOrderLine> lines = salesOrderLineRepository.findBySalesOrderId(order.getId());
        for (SalesOrderLine line : lines) {
            int qtyToReserve = line.getQuantity();
            List<Inventory> inventories = inventoryRepository.findByProductId(line.getProduct().getId()).stream()
                    .sorted(Comparator.comparingInt(inv -> -((inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0) - (inv.getQtyReserved() != null ? inv.getQtyReserved() : 0))))
                    .toList();

            for (Inventory inv : inventories) {
                int available = (inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0) - (inv.getQtyReserved() != null ? inv.getQtyReserved() : 0);
                if (available <= 0) continue;
                int take = Math.min(available, qtyToReserve);
                inv.setQtyReserved((inv.getQtyReserved() != null ? inv.getQtyReserved() : 0) + take);
                inv.setQtyOnHand((inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0) - take);
                inventoryRepository.save(inv);
                qtyToReserve -= take;
                if (qtyToReserve <= 0) break;
            }

            if (qtyToReserve > 0) {
                line.setBackorder(true);
                salesOrderLineRepository.save(line);
            }
        }

        order.setStatus(OrderStatus.RESERVED);
        return salesOrderRepository.save(order);
    }

    @Override
    @Transactional
    public SalesOrder ship(UUID id) {
        @SuppressWarnings("null")
        SalesOrder order = salesOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.RESERVED) {
            throw new RuntimeException("Only RESERVED orders can be shipped");
        }
        order.setStatus(OrderStatus.SHIPPED);
        order.setShippedAt(LocalDateTime.now());
        return salesOrderRepository.save(order);
    }

    @Override
    @Transactional
    public SalesOrder deliver(UUID id) {
        @SuppressWarnings("null")
        SalesOrder order = salesOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("Only SHIPPED orders can be delivered");
        }
        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());
        return salesOrderRepository.save(order);
    }

    @Override
    @Transactional
    public SalesOrder cancel(UUID id) {
        @SuppressWarnings("null")
        SalesOrder order = salesOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        // release reserved quantities
        List<SalesOrderLine> lines = salesOrderLineRepository.findBySalesOrderId(order.getId());
        for (SalesOrderLine line : lines) {
            int qtyToRelease = line.getQuantity();
            List<Inventory> inventories = inventoryRepository.findByProductId(line.getProduct().getId());
            // naive: add qty back to first inventory entries that have reserved amounts
            for (Inventory inv : inventories) {
                int reserved = inv.getQtyReserved() != null ? inv.getQtyReserved() : 0;
                if (reserved <= 0) continue;
                int give = Math.min(reserved, qtyToRelease);
                inv.setQtyReserved(reserved - give);
                inv.setQtyOnHand((inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0) + give);
                inventoryRepository.save(inv);
                qtyToRelease -= give;
                if (qtyToRelease <= 0) break;
            }
        }
        order.setStatus(OrderStatus.CANCELED);
        return salesOrderRepository.save(order);
    }
}
