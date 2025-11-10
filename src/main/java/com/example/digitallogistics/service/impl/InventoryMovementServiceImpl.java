package com.example.digitallogistics.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    public List<InventoryMovement> findAll(Optional<MovementType> type) {
        if (type.isPresent()) {
            return inventoryMovementRepository.findByType(type.get());
        }
        return inventoryMovementRepository.findAll();
    }

    protected InventoryMovement recordMovement(MovementType type, InventoryMovementCreateDto dto) {
        // adjust inventory qtyOnHand according to type
        int adj = dto.quantity != null ? dto.quantity : 0;
        if (type == MovementType.OUTBOUND) adj = -adj;

        // use inventory repository adjust: find inventory and update qtyOnHand
        List<Inventory> inventories = inventoryRepository.findByWarehouseId(dto.warehouseId).stream()
                .filter(i -> i.getProduct() != null && i.getProduct().getId().equals(dto.productId))
                .collect(Collectors.toList());

        if (!inventories.isEmpty()) {
            Inventory inv = inventories.get(0);
            inv.setQtyOnHand((inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0) + adj);
            inventoryRepository.save(inv);
        } else {
            // no inventory line for that warehouse/product: create a minimal record
            Inventory inv = Inventory.builder()
                    .warehouse(null)
                    .product(null)
                    .qtyOnHand(adj)
                    .qtyReserved(0)
                    .build();
            // note: product and warehouse left null to avoid FK errors; better to create properly when available
            inventoryRepository.save(inv);
        }

        InventoryMovement m = InventoryMovement.builder()
                .type(type)
                .quantity(dto.quantity)
                .occurredAt(LocalDateTime.now())
                .reference(dto.reference)
                .description(dto.description)
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
