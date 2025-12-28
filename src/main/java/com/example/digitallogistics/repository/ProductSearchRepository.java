package com.example.digitallogistics.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.digitallogistics.model.search.ProductSearchDocument;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearchDocument, String> {

    List<ProductSearchDocument> findByNameContainingIgnoreCase(String name);
}


