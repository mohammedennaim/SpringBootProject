package com.example.digitallogistics.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.model.entity.Warehouse;
import com.example.digitallogistics.repository.InventoryRepository;
import com.example.digitallogistics.repository.ProductRepository;
import com.example.digitallogistics.repository.WarehouseRepository;
import com.example.digitallogistics.service.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, 
                               WarehouseRepository warehouseRepository,
                               ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public List<Inventory> findByWarehouseId(UUID warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId);
    }

    @Override
    public List<Inventory> findByProductId(UUID productId) {
        return inventoryRepository.findByProductId(productId);
    }

    @Override
    public Optional<Inventory> findById(UUID id) {
        return inventoryRepository.findById(id);
    }

    @Override
    public Optional<Inventory> findByWarehouseAndProduct(UUID warehouseId, UUID productId) {
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getWarehouse().getId().equals(warehouseId) && 
                              inv.getProduct().getId().equals(productId))
                .findFirst();
    }

    @Override
    public Inventory adjustInventory(UUID warehouseId, UUID productId, Integer adjustmentQty, String reason) {
        Optional<Inventory> existingInventory = findByWarehouseAndProduct(warehouseId, productId);
        
        if (existingInventory.isPresent()) {
            // Update existing inventory
            Inventory inventory = existingInventory.get();
            int currentQty = inventory.getQtyOnHand() != null ? inventory.getQtyOnHand() : 0;
            inventory.setQtyOnHand(currentQty + adjustmentQty);
            return inventoryRepository.save(inventory);
        } else {
            // Create new inventory record
            Optional<Warehouse> warehouse = warehouseRepository.findById(warehouseId);
            Optional<Product> product = productRepository.findById(productId);
            
            if (warehouse.isPresent() && product.isPresent()) {
                Inventory newInventory = Inventory.builder()
                        .warehouse(warehouse.get())
                        .product(product.get())
                        .qtyOnHand(Math.max(0, adjustmentQty)) // Don't allow negative starting quantities
                        .qtyReserved(0)
                        .build();
                return inventoryRepository.save(newInventory);
            } else {
                throw new RuntimeException("Warehouse or Product not found");
            }
        }
    }

    @Override
    public Integer getAvailableQuantity(UUID productId) {
        List<Inventory> inventories = findByProductId(productId);
        return inventories.stream()
                .mapToInt(inv -> {
                    int onHand = inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0;
                    int reserved = inv.getQtyReserved() != null ? inv.getQtyReserved() : 0;
                    return Math.max(0, onHand - reserved);
                })
                .sum();
    }

    @Override
    public Integer getAvailableQuantityInWarehouse(UUID warehouseId, UUID productId) {
        Optional<Inventory> inventory = findByWarehouseAndProduct(warehouseId, productId);
        if (inventory.isPresent()) {
            Inventory inv = inventory.get();
            int onHand = inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0;
            int reserved = inv.getQtyReserved() != null ? inv.getQtyReserved() : 0;
            return Math.max(0, onHand - reserved);
        }
        return 0;
    }
}