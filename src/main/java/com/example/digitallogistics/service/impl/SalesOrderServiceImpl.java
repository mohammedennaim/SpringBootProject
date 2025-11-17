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
import com.example.digitallogistics.exception.ValidationException;
import com.example.digitallogistics.exception.ResourceNotFoundException;
import com.example.digitallogistics.exception.OrderStateException;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderLineRepository salesOrderLineRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
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

    @Override
    @Transactional
    public SalesOrder create(SalesOrderCreateDto dto) {
        validateInventoryAvailability(dto.getLines());
        
        SalesOrder order = createOrderEntity(dto);
        SalesOrder saved = salesOrderRepository.save(order);
        
        List<SalesOrderLine> lines = createOrderLines(dto.getLines(), saved);
        reserveInventoryForLines(lines);
        
        saved.setStatus(OrderStatus.RESERVED);
        return salesOrderRepository.save(saved);
    }

    @Override
    public Optional<SalesOrder> findById(UUID id) {
        return salesOrderRepository.findById(id);
    }

    @Override
    public List<SalesOrderLine> findLines(UUID orderId) {
        return salesOrderLineRepository.findBySalesOrderId(orderId);
    }

    @Override
    @Transactional
    public SalesOrder reserve(UUID id) {
        SalesOrder order = findOrderById(id);
        validateOrderStatus(order, OrderStatus.CREATED, "Only CREATED orders can be reserved");
        
        List<SalesOrderLine> lines = salesOrderLineRepository.findBySalesOrderId(order.getId());
        reserveInventoryForLines(lines);
        
        order.setStatus(OrderStatus.RESERVED);
        return salesOrderRepository.save(order);
    }

    @Override
    @Transactional
    public SalesOrder ship(UUID id) {
        SalesOrder order = findOrderById(id);
        validateOrderStatus(order, OrderStatus.RESERVED, "Only RESERVED orders can be shipped");
        
        order.setStatus(OrderStatus.SHIPPED);
        order.setShippedAt(LocalDateTime.now());
        return salesOrderRepository.save(order);
    }

    @Override
    @Transactional
    public SalesOrder deliver(UUID id) {
        SalesOrder order = findOrderById(id);
        validateOrderStatus(order, OrderStatus.SHIPPED, "Only SHIPPED orders can be delivered");
        
        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());
        return salesOrderRepository.save(order);
    }

    @Override
    @Transactional
    public SalesOrder cancel(UUID id) {
        SalesOrder order = findOrderById(id);
        List<SalesOrderLine> lines = salesOrderLineRepository.findBySalesOrderId(order.getId());
        releaseReservedInventory(lines);
        
        order.setStatus(OrderStatus.CANCELED);
        return salesOrderRepository.save(order);
    }
    
    private void validateInventoryAvailability(List<SalesOrderLineCreateDto> lines) {
        for (SalesOrderLineCreateDto line : lines) {
            int totalOnHand = inventoryRepository.findByProductId(line.getProductId()).stream()
                    .mapToInt(inv -> inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0)
                    .sum();
            if (line.getQuantity() > totalOnHand) {
                String productRef = getProductReference(line.getProductId());
                throw new ValidationException("Insufficient inventory for product " + productRef + 
                    ": requested=" + line.getQuantity() + ", available=" + totalOnHand);
            }
        }
    }
    
    private SalesOrder createOrderEntity(SalesOrderCreateDto dto) {
        SalesOrder order = SalesOrder.builder()
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();
        clientRepository.findById(dto.getClientId()).ifPresent(order::setClient);
        return order;
    }
    
    private List<SalesOrderLine> createOrderLines(List<SalesOrderLineCreateDto> lineDtos, SalesOrder order) {
        List<SalesOrderLine> lines = new ArrayList<>();
        for (SalesOrderLineCreateDto lineDto : lineDtos) {
            Product product = productRepository.findById(lineDto.getProductId()).orElse(null);
            SalesOrderLine line = SalesOrderLine.builder()
                    .product(product)
                    .quantity(lineDto.getQuantity())
                    .unitPrice(product != null ? product.getUnitPrice() : null)
                    .salesOrder(order)
                    .backorder(false)
                    .build();
            lines.add(salesOrderLineRepository.save(line));
        }
        return lines;
    }
    
    private void reserveInventoryForLines(List<SalesOrderLine> lines) {
        for (SalesOrderLine line : lines) {
            if (line.getProduct() == null) {
                throw new ValidationException("Product is required for sales order line");
            }
            reserveInventoryForLine(line);
        }
    }
    
    private void reserveInventoryForLine(SalesOrderLine line) {
        int qtyToReserve = line.getQuantity();
        List<Inventory> inventories = getSortedInventories(line.getProduct().getId());
        
        qtyToReserve = processInventoryReservation(inventories, qtyToReserve);
        
        if (qtyToReserve > 0) {
            line.setBackorder(true);
            salesOrderLineRepository.save(line);
        }
    }
    
    private void releaseReservedInventory(List<SalesOrderLine> lines) {
        for (SalesOrderLine line : lines) {
            if (line.getProduct() != null) {
                releaseInventoryForLine(line);
            }
        }
    }
    
    private void releaseInventoryForLine(SalesOrderLine line) {
        int qtyToRelease = line.getQuantity();
        List<Inventory> inventories = inventoryRepository.findByProductId(line.getProduct().getId());
        
        for (Inventory inventory : inventories) {
            qtyToRelease = releaseFromInventory(inventory, qtyToRelease);
            if (qtyToRelease <= 0) return;
        }
    }
    
    private int releaseFromInventory(Inventory inventory, int qtyToRelease) {
        int reserved = inventory.getQtyReserved() != null ? inventory.getQtyReserved() : 0;
        if (reserved <= 0) return qtyToRelease;
        
        int give = Math.min(reserved, qtyToRelease);
        int currentOnHand = inventory.getQtyOnHand() != null ? inventory.getQtyOnHand() : 0;
        
        inventory.setQtyReserved(reserved - give);
        inventory.setQtyOnHand(currentOnHand + give);
        inventoryRepository.save(inventory);
        
        return qtyToRelease - give;
    }
    
    private SalesOrder findOrderById(UUID id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found with id: " + id));
    }
    
    private void validateOrderStatus(SalesOrder order, OrderStatus expectedStatus, String errorMessage) {
        if (order.getStatus() != expectedStatus) {
            throw new OrderStateException(errorMessage);
        }
    }
    
    private String getProductReference(UUID productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return productId.toString();
        }
        return product.getSku() != null ? product.getSku() : product.getId().toString();
    }
    
    private List<Inventory> getSortedInventories(UUID productId) {
        return inventoryRepository.findByProductId(productId).stream()
                .sorted(Comparator.comparingInt(inv -> -(inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0)))
                .toList();
    }
    
    private int processInventoryReservation(List<Inventory> inventories, int qtyToReserve) {
        for (Inventory inventory : inventories) {
            qtyToReserve = reserveFromInventory(inventory, qtyToReserve);
            if (qtyToReserve <= 0) break;
        }
        return qtyToReserve;
    }
    
    private int reserveFromInventory(Inventory inventory, int qtyToReserve) {
        int onHand = inventory.getQtyOnHand() != null ? inventory.getQtyOnHand() : 0;
        if (onHand <= 0) return qtyToReserve;
        
        int take = Math.min(onHand, qtyToReserve);
        int currentReserved = inventory.getQtyReserved() != null ? inventory.getQtyReserved() : 0;
        
        inventory.setQtyReserved(currentReserved + take);
        inventory.setQtyOnHand(onHand - take);
        inventoryRepository.save(inventory);
        
        return qtyToReserve - take;
    }
}
