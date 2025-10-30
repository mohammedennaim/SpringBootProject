package com.example.digitallogistics.model.entity;

import java.time.LocalDateTime;

import com.example.digitallogistics.model.enums.MovementType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private MovementType type;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "occurred_at")
	private LocalDateTime occurredAt;

	@Column(name = "reference_document")
	private String reference;

	@Column(name = "description")
	private String description;
}
