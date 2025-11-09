package com.example.digitallogistics.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.model.dto.PurchaseOrderCreateDto;
import com.example.digitallogistics.model.dto.PurchaseOrderDto;
import com.example.digitallogistics.model.dto.PurchaseOrderReceiveDto;
import com.example.digitallogistics.model.entity.PurchaseOrder;
import com.example.digitallogistics.model.mapper.PurchaseOrderMapper;
import com.example.digitallogistics.model.mapper.SupplierMapper;
import com.example.digitallogistics.model.enums.PurchaseOrderStatus;
import com.example.digitallogistics.service.PurchaseOrderService;
import com.example.digitallogistics.service.PurchaseOrderService;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final SupplierMapper supplierMapper;
    private final com.example.digitallogistics.repository.PurchaseOrderLineRepository purchaseOrderLineRepository;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService, SupplierMapper supplierMapper,
            com.example.digitallogistics.repository.PurchaseOrderLineRepository purchaseOrderLineRepository) {
        this.purchaseOrderService = purchaseOrderService;
        this.supplierMapper = supplierMapper;
        this.purchaseOrderLineRepository = purchaseOrderLineRepository;
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderDto>> list(@RequestParam(required = false) UUID supplierId,
            @RequestParam(required = false) PurchaseOrderStatus status) {
        List<PurchaseOrder> pos = purchaseOrderService.findAll(Optional.ofNullable(supplierId), Optional.ofNullable(status));
        List<PurchaseOrderDto> dtos = pos.stream().map(p -> PurchaseOrderMapper.toDto(p, supplierMapper, null)).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderDto> create(@RequestBody @Valid PurchaseOrderCreateDto dto) {
    PurchaseOrder created = purchaseOrderService.create(dto);
    java.util.List<com.example.digitallogistics.model.entity.PurchaseOrderLine> lines = purchaseOrderLineRepository.findByPurchaseOrderId(created.getId());
    return ResponseEntity.ok(PurchaseOrderMapper.toDto(created, supplierMapper, lines));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDto> get(@PathVariable UUID id) {
        return purchaseOrderService.findById(id).map(po -> {
            java.util.List<com.example.digitallogistics.model.entity.PurchaseOrderLine> lines = purchaseOrderLineRepository.findByPurchaseOrderId(po.getId());
            return ResponseEntity.ok(PurchaseOrderMapper.toDto(po, supplierMapper, lines));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<PurchaseOrderDto> approve(@PathVariable UUID id) {
    PurchaseOrder updated = purchaseOrderService.approve(id);
    java.util.List<com.example.digitallogistics.model.entity.PurchaseOrderLine> lines = purchaseOrderLineRepository.findByPurchaseOrderId(updated.getId());
    return ResponseEntity.ok(PurchaseOrderMapper.toDto(updated, supplierMapper, lines));
    }

    @PutMapping("/{id}/receive")
    public ResponseEntity<PurchaseOrderDto> receive(@PathVariable UUID id, @RequestBody @Valid PurchaseOrderReceiveDto dto) {
    PurchaseOrder updated = purchaseOrderService.receive(id, dto);
    java.util.List<com.example.digitallogistics.model.entity.PurchaseOrderLine> lines = purchaseOrderLineRepository.findByPurchaseOrderId(updated.getId());
    return ResponseEntity.ok(PurchaseOrderMapper.toDto(updated, supplierMapper, lines));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<PurchaseOrderDto> cancel(@PathVariable UUID id) {
    PurchaseOrder updated = purchaseOrderService.cancel(id);
    java.util.List<com.example.digitallogistics.model.entity.PurchaseOrderLine> lines = purchaseOrderLineRepository.findByPurchaseOrderId(updated.getId());
    return ResponseEntity.ok(PurchaseOrderMapper.toDto(updated, supplierMapper, lines));
    }
}
