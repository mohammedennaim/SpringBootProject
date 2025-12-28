package com.example.digitallogistics.service;

import java.util.List;

import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.model.search.ProductSearchDocument;

public interface ProductSearchService {

    void indexProduct(Product product);

    void deleteProduct(String id);

    List<ProductSearchDocument> searchByName(String query);
}


