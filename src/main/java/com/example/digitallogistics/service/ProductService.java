package com.example.digitallogistics.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.digitallogistics.model.entity.Product;

public interface ProductService {
    Product create(Product product);
    Optional<Product> findById(UUID id);
    Optional<Product> findBySku(String sku);
    Optional<Product> update(UUID id, Product product);
    void deleteById(UUID id);
    Page<Product> findAll(Pageable pageable, String search, Boolean active);
    boolean desactivate(String sku);
}
