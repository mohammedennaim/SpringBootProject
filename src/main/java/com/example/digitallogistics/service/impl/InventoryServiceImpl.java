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

    @SuppressWarnings("null")
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

    @SuppressWarnings("null")
    @Override
    public Inventory adjustInventory(UUID warehouseId, UUID productId, Integer adjustmentQty, String reason) {
        Optional<Inventory> existingInventory = findByWarehouseAndProduct(warehouseId, productId);
        
        if (existingInventory.isPresent()) {
            Inventory inventory = existingInventory.get();
            int currentQty = inventory.getQtyOnHand() != null ? inventory.getQtyOnHand() : 0;
            inventory.setQtyOnHand(currentQty + adjustmentQty);
            return inventoryRepository.save(inventory);
        } else {
            Optional<Warehouse> warehouse = warehouseRepository.findById(warehouseId);
            Optional<Product> product = productRepository.findById(productId);
            
            if (warehouse.isPresent() && product.isPresent()) {
                Inventory newInventory = Inventory.builder()
                        .warehouse(warehouse.get())
                        .product(product.get())
                        .qtyOnHand(Math.max(0, adjustmentQty))
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

    @SuppressWarnings("null")
    @Override
    public void adjustProductTotal(UUID productId, int targetTotal) {
        List<Inventory> inventories = findByProductId(productId);
        int currentTotal = inventories.stream().mapToInt(i -> i.getQtyOnHand() != null ? i.getQtyOnHand() : 0).sum();
        int reservedTotal = inventories.stream().mapToInt(i -> i.getQtyReserved() != null ? i.getQtyReserved() : 0).sum();

        if (targetTotal < reservedTotal) {
            throw new RuntimeException("Cannot set product total below reserved quantity: " + reservedTotal);
        }

        int delta = targetTotal - currentTotal;

        if (delta == 0) return;

        if (delta > 0) {
            java.util.List<Warehouse> mains = warehouseRepository.findByCode("MAIN");
            Warehouse main = mains.isEmpty() ? null : mains.get(0);

            Inventory targetInv = null;
            if (main != null) {
                for (Inventory inv : inventories) {
                    if (inv.getWarehouse() != null && main.getId().equals(inv.getWarehouse().getId())) {
                        targetInv = inv; break;
                    }
                }
            }

            if (targetInv != null) {
                int current = targetInv.getQtyOnHand() != null ? targetInv.getQtyOnHand() : 0;
                targetInv.setQtyOnHand(current + delta);
                inventoryRepository.save(targetInv);
            } else if (main != null) {
                java.util.Optional<Product> p = productRepository.findById(productId);
                if (p.isPresent()) {
                    Inventory newInv = Inventory.builder()
                            .warehouse(main)
                            .product(p.get())
                            .qtyOnHand(Math.max(0, delta))
                            .qtyReserved(0)
                            .build();
                    inventoryRepository.save(newInv);
                } else {
                    throw new RuntimeException("Product not found");
                }
            } else {
                if (!inventories.isEmpty()) {
                    Inventory inv0 = inventories.get(0);
                    int cur = inv0.getQtyOnHand() != null ? inv0.getQtyOnHand() : 0;
                    inv0.setQtyOnHand(cur + delta);
                    inventoryRepository.save(inv0);
                } else {
                    throw new RuntimeException("No warehouse available to add inventory");
                }
            }
        } else {
            int toRemove = -delta;
            inventories.sort((a,b) -> Integer.compare(
                    Math.max(0, (b.getQtyOnHand() != null ? b.getQtyOnHand() : 0) - (b.getQtyReserved() != null ? b.getQtyReserved() : 0)),
                    Math.max(0, (a.getQtyOnHand() != null ? a.getQtyOnHand() : 0) - (a.getQtyReserved() != null ? a.getQtyReserved() : 0))
            ));

            for (Inventory inv : inventories) {
                int onHand = inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0;
                int reserved = inv.getQtyReserved() != null ? inv.getQtyReserved() : 0;
                int available = Math.max(0, onHand - reserved);
                if (available <= 0) continue;
                int take = Math.min(available, toRemove);
                inv.setQtyOnHand(onHand - take);
                inventoryRepository.save(inv);
                toRemove -= take;
                if (toRemove <= 0) break;
            }

            if (toRemove > 0) {
                throw new RuntimeException("Unable to reduce inventories to requested total; remaining=" + toRemove);
            }
        }

    }

    @SuppressWarnings("null")
    @Override
    public Inventory updateInventory(UUID id, UUID warehouseId, UUID productId, Integer qtyOnHand, Integer qtyReserved) {
        Optional<Inventory> opt = inventoryRepository.findById(id);
        if (opt.isEmpty()) {
            Optional<Warehouse> wh = warehouseRepository.findById(warehouseId);
            Optional<Product> p = productRepository.findById(productId);
            if (wh.isEmpty()) throw new RuntimeException("Warehouse not found");
            if (p.isEmpty()) throw new RuntimeException("Product not found");
            Inventory newInv = Inventory.builder()
                    .id(id)
                    .warehouse(wh.get())
                    .product(p.get())
                    .qtyOnHand(qtyOnHand != null ? qtyOnHand : 0)
                    .qtyReserved(qtyReserved != null ? qtyReserved : 0)
                    .build();
            return inventoryRepository.save(newInv);
        }

        Inventory inv = opt.get();

        if (warehouseId != null) {
            Optional<Warehouse> wh = warehouseRepository.findById(warehouseId);
            if (wh.isEmpty()) throw new RuntimeException("Warehouse not found");
            inv.setWarehouse(wh.get());
        }

        if (productId != null) {
            Optional<Product> p = productRepository.findById(productId);
            if (p.isEmpty()) throw new RuntimeException("Product not found");
            inv.setProduct(p.get());
        }

        if (qtyOnHand != null) inv.setQtyOnHand(qtyOnHand);
        if (qtyReserved != null) inv.setQtyReserved(qtyReserved);

        return inventoryRepository.save(inv);
    }
}