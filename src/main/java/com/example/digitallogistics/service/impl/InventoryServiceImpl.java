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

    private static final String PRODUCT_NOT_FOUND = "Product not found";
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
                .filter(inv -> inv.getWarehouse() != null && inv.getProduct() != null &&
                              inv.getWarehouse().getId().equals(warehouseId) && 
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

    @Override
    @SuppressWarnings("null")
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
            increaseInventory(productId, inventories, delta);
        } else {
            decreaseInventory(inventories, -delta);
        }
    }

    private void increaseInventory(UUID productId, List<Inventory> inventories, int delta) {
        Warehouse main = warehouseRepository.findByCode("MAIN").stream().findFirst().orElse(null);
        Inventory targetInv = findInventoryInWarehouse(inventories, main);

        if (targetInv != null) {
            updateInventoryQuantity(targetInv, delta);
        } else if (main != null) {
            createNewInventory(productId, main, delta);
        } else {
            addToFirstInventory(inventories, delta);
        }
    }

    private Inventory findInventoryInWarehouse(List<Inventory> inventories, Warehouse warehouse) {
        if (warehouse == null) return null;
        return inventories.stream()
            .filter(inv -> inv.getWarehouse() != null && warehouse.getId().equals(inv.getWarehouse().getId()))
            .findFirst()
            .orElse(null);
    }

    private void updateInventoryQuantity(Inventory inventory, int delta) {
        int current = inventory.getQtyOnHand() != null ? inventory.getQtyOnHand() : 0;
        inventory.setQtyOnHand(current + delta);
        inventoryRepository.save(inventory);
    }

    private void createNewInventory(UUID productId, Warehouse warehouse, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException(PRODUCT_NOT_FOUND));
        Inventory newInv = Inventory.builder()
            .warehouse(warehouse)
            .product(product)
            .qtyOnHand(Math.max(0, quantity))
            .qtyReserved(0)
            .build();
        inventoryRepository.save(newInv);
    }

    private void addToFirstInventory(List<Inventory> inventories, int delta) {
        if (inventories.isEmpty()) {
            throw new RuntimeException("No warehouse available to add inventory");
        }
        updateInventoryQuantity(inventories.get(0), delta);
    }

    private void decreaseInventory(List<Inventory> inventories, int toRemove) {
        inventories.sort((a, b) -> Integer.compare(getAvailableQty(b), getAvailableQty(a)));

        for (Inventory inv : inventories) {
            int available = getAvailableQty(inv);
            if (available <= 0) continue;
            
            int take = Math.min(available, toRemove);
            int onHand = inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0;
            inv.setQtyOnHand(onHand - take);
            inventoryRepository.save(inv);
            toRemove -= take;
            if (toRemove <= 0) break;
        }

        if (toRemove > 0) {
            throw new RuntimeException("Unable to reduce inventories to requested total; remaining=" + toRemove);
        }
    }

    private int getAvailableQty(Inventory inv) {
        int onHand = inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0;
        int reserved = inv.getQtyReserved() != null ? inv.getQtyReserved() : 0;
        return Math.max(0, onHand - reserved);
    }

    @SuppressWarnings("null")
    @Override
    public Inventory updateInventory(UUID id, UUID warehouseId, UUID productId, Integer qtyOnHand, Integer qtyReserved) {
        return inventoryRepository.findById(id)
            .map(inv -> updateExistingInventory(inv, warehouseId, productId, qtyOnHand, qtyReserved))
            .orElseGet(() -> createInventory(id, warehouseId, productId, qtyOnHand, qtyReserved));
    }

    private Inventory updateExistingInventory(Inventory inv, UUID warehouseId, UUID productId, Integer qtyOnHand, Integer qtyReserved) {
        if (warehouseId != null) {
            Warehouse wh = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
            inv.setWarehouse(wh);
        }
        if (productId != null) {
            Product p = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException(PRODUCT_NOT_FOUND));
            inv.setProduct(p);
        }
        if (qtyOnHand != null) inv.setQtyOnHand(qtyOnHand);
        if (qtyReserved != null) inv.setQtyReserved(qtyReserved);
        return inventoryRepository.save(inv);
    }

    private Inventory createInventory(UUID id, UUID warehouseId, UUID productId, Integer qtyOnHand, Integer qtyReserved) {
        Warehouse wh = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        Product p = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException(PRODUCT_NOT_FOUND));
        Inventory newInv = Inventory.builder()
            .id(id)
            .warehouse(wh)
            .product(p)
            .qtyOnHand(qtyOnHand != null ? qtyOnHand : 0)
            .qtyReserved(qtyReserved != null ? qtyReserved : 0)
            .build();
        return inventoryRepository.save(newInv);
    }
}