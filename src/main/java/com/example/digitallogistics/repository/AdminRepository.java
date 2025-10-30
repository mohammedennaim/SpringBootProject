package com.example.digitallogistics.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.digitallogistics.model.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
}
