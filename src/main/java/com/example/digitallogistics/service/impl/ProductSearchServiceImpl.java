package com.example.digitallogistics.service.impl;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.model.search.ProductSearchDocument;
import com.example.digitallogistics.repository.ProductSearchRepository;
import com.example.digitallogistics.service.ProductSearchService;

@Service
@ConditionalOnBean(ProductSearchRepository.class)
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ProductSearchRepository productSearchRepository;

    public ProductSearchServiceImpl(ProductSearchRepository productSearchRepository) {
        this.productSearchRepository = productSearchRepository;
    }

    @Override
    public void indexProduct(Product product) {
        ProductSearchDocument doc = ProductSearchDocument.builder()
                .id(product.getId() != null ? product.getId().toString() : null)
                .sku(product.getSku())
                .name(product.getName())
                .category(product.getCategory())
                .build();
        productSearchRepository.save(doc);
    }

    @Override
    public void deleteProduct(String id) {
        productSearchRepository.deleteById(id);
    }

    @Override
    public List<ProductSearchDocument> searchByName(String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return List.of();
            }
            return productSearchRepository.findByNameContainingIgnoreCase(query.trim());
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche Elasticsearch: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}


