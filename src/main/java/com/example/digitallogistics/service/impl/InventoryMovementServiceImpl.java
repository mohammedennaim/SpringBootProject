package com.example.digitallogistics.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.digitallogistics.model.dto.InventoryMovementCreateDto;
import com.example.digitallogistics.model.entity.InventoryMovement;
import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.enums.MovementType;
import com.example.digitallogistics.repository.InventoryMovementRepository;
import com.example.digitallogistics.repository.InventoryRepository;
import com.example.digitallogistics.service.InventoryMovementService;

@Service
public class InventoryMovementServiceImpl implements InventoryMovementService {

    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryRepository inventoryRepository;

    public InventoryMovementServiceImpl(InventoryMovementRepository inventoryMovementRepository,
            InventoryRepository inventoryRepository) {
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<InventoryMovement> findAll() {
        return inventoryMovementRepository.findAll();
    }

    @SuppressWarnings("null")
    protected InventoryMovement recordMovement(MovementType type, InventoryMovementCreateDto dto) {
        int adj = dto.getQuantity() != null ? dto.getQuantity() : 0;
        if (type == MovementType.OUTBOUND) adj = -adj;

        List<Inventory> inventories = inventoryRepository.findByWarehouseId(dto.getWarehouseId()).stream()
                .filter(i -> i.getProduct() != null && i.getProduct().getId().equals(dto.getProductId()))
                .collect(Collectors.toList());

        if (!inventories.isEmpty()) {
            Inventory inv = inventories.get(0);
            inv.setQtyOnHand((inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0) + adj);
            inventoryRepository.save(inv);
        } else {
            Inventory inv = Inventory.builder()
                    .warehouse(null)
                    .product(null)
                    .qtyOnHand(adj)
                    .qtyReserved(0)
                    .build();
            inventoryRepository.save(inv);
        }

        InventoryMovement m = InventoryMovement.builder()
                .type(type)
        .quantity(dto.getQuantity())
                .occurredAt(LocalDateTime.now())
        .reference(dto.getReference())
        .description(dto.getDescription())
                .build();
        return inventoryMovementRepository.save(m);
    }

    @Override
    @Transactional
    public InventoryMovement recordInbound(InventoryMovementCreateDto dto) {
        return recordMovement(MovementType.INBOUND, dto);
    }

    @Override
    @Transactional
    public InventoryMovement recordOutbound(InventoryMovementCreateDto dto) {
        return recordMovement(MovementType.OUTBOUND, dto);
    }

    @Override
    @Transactional
    public InventoryMovement recordAdjustment(InventoryMovementCreateDto dto) {
        return recordMovement(MovementType.ADJUSTMENT, dto);
    }
}
