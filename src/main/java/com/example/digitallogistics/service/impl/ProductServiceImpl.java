package com.example.digitallogistics.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.repository.ProductRepository;
import com.example.digitallogistics.service.ProductService;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku);
    }

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

    @Override
    public void deleteById(UUID id) {
        // logical delete: mark inactive
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
