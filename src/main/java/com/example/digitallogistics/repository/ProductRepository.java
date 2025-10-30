package com.example.digitallogistics.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findBySku(String sku);
    List<Product> findByNameContainingIgnoreCase(String namePart);
    List<Product> findByCategory(String category);
    List<Product> findByActiveTrue();
}
