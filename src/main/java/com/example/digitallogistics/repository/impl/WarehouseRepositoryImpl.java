package com.example.digitallogistics.repository.impl;

import com.example.digitallogistics.model.entity.Warehouse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class WarehouseRepositoryImpl {

    @PersistenceContext
    private EntityManager em;

    public List<Warehouse> findAll() {

        try {;
            TypedQuery<Warehouse> q = em.createQuery("SELECT w FROM Warehouse w", Warehouse.class);
            return q.getResultList();
        } catch (IllegalArgumentException ex) {
            return Collections.emptyList();
        }
    }
}
