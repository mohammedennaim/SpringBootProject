package com.example.digitallogistics.repository.impl;

import java.util.Collections;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.model.enums.Role;

@Repository
public class UserRepositoryImpl {

    @PersistenceContext
    private EntityManager em;

    public List<User> findByRole(Role role) {
        if (role == null) {
            return Collections.emptyList();
        }
        try {;
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class);
            q.setParameter("role", role);
            return q.getResultList();
        } catch (IllegalArgumentException ex) {
            return Collections.emptyList();
        }
    }
}
