package com.example.digitallogistics.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.digitallogistics.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findBySku(String sku);

    Page<Product> findBySkuContainingIgnoreCaseOrNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String sku, String name, String category, Pageable pageable);

    Page<Product> findByActiveAndSkuContainingIgnoreCaseOrActiveAndNameContainingIgnoreCaseOrActiveAndCategoryContainingIgnoreCase(
            boolean active1, String sku, boolean active2, String name, boolean active3, String category, Pageable pageable);
}
