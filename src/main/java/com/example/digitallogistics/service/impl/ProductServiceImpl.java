package com.example.digitallogistics.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.repository.ProductRepository;
import com.example.digitallogistics.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final com.example.digitallogistics.service.InventoryService inventoryService;

    public ProductServiceImpl(ProductRepository productRepository, com.example.digitallogistics.service.InventoryService inventoryService) {
        this.productRepository = productRepository;
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
}
