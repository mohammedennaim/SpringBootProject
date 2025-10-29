package com.example.digitallogistics.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.digitallogistics.model.entity.InventoryMovement;
import com.example.digitallogistics.model.enums.MovementType;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findByType(MovementType type);
}
