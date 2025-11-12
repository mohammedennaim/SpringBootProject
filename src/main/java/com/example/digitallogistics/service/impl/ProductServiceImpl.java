package com.example.digitallogistics.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.entity.SalesOrderLine;
import com.example.digitallogistics.model.enums.OrderStatus;
import com.example.digitallogistics.repository.InventoryRepository;
import com.example.digitallogistics.repository.SalesOrderLineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.repository.ProductRepository;
import com.example.digitallogistics.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final SalesOrderLineRepository salesOrderLineRepository;
    private final com.example.digitallogistics.service.InventoryService inventoryService;

    public ProductServiceImpl(ProductRepository productRepository, InventoryRepository inventoryRepository, SalesOrderLineRepository salesOrderLineRepository, com.example.digitallogistics.service.InventoryService inventoryService) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.salesOrderLineRepository = salesOrderLineRepository;
        this.inventoryService = inventoryService;
    }

    @SuppressWarnings("null")
    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Product> update(UUID id, Product product) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(product.getName());
            existing.setCategory(product.getCategory());
            existing.setUnitPrice(product.getUnitPrice());
            existing.setActive(product.isActive());
            return productRepository.save(existing);
        });
    }

    @SuppressWarnings("null")
    @Override
    public void deleteById(UUID id) {
        productRepository.findById(id).ifPresent(p -> {
            p.setActive(false);
            productRepository.save(p);
        });
    }

    @Override
    public Page<Product> findAll(Pageable pageable, String search, Boolean active) {
        String term = (search == null) ? "" : search.trim();
        if (active == null) {
            return productRepository.findBySkuContainingIgnoreCaseOrNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                    term, term, term, pageable);
        }

        return productRepository.findByActiveAndSkuContainingIgnoreCaseOrActiveAndNameContainingIgnoreCaseOrActiveAndCategoryContainingIgnoreCase(
                active, term, active, term, active, term, pageable);
    }
    @Override
    public boolean desactivate(String sku){
        Optional<Product> optProduct = productRepository.findBySku(sku);
        if (optProduct.isEmpty()) return false;
        Product product = optProduct.get();
        List<Inventory> inventories = inventoryRepository.findByProductId(product.getId());
        List<Inventory> inventoriesFilter = inventories.stream().filter(i -> i.getQtyReserved()>0).toList();
        List<SalesOrderLine> salesOrderLines = salesOrderLineRepository.findByProductId(product.getId());
        List<SalesOrderLine> salesOrderLinesFilter = salesOrderLines.stream().filter(s->s.getSalesOrder().getStatus().equals(OrderStatus.RESERVED) || s.getSalesOrder().getStatus().equals(OrderStatus.CREATED)).toList();
        return inventoriesFilter.isEmpty() || salesOrderLinesFilter.isEmpty();
    }
}
