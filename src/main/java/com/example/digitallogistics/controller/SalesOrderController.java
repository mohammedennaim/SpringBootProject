package com.example.digitallogistics.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.model.dto.SalesOrderCreateDto;
import com.example.digitallogistics.model.dto.SalesOrderDto;
import com.example.digitallogistics.model.entity.SalesOrder;
import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.model.enums.Role;
import com.example.digitallogistics.model.mapper.ClientMapper;
import com.example.digitallogistics.model.mapper.SalesOrderMapper;
import com.example.digitallogistics.model.enums.OrderStatus;
import com.example.digitallogistics.service.SalesOrderService;
import com.example.digitallogistics.model.entity.SalesOrderLine;
import com.example.digitallogistics.repository.UserRepository;
import com.example.digitallogistics.security.SecurityUtils;

@RestController
@RequestMapping("/api/sales-orders")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;
    private final ClientMapper clientMapper;
    private final UserRepository userRepository;

    public SalesOrderController(SalesOrderService salesOrderService, ClientMapper clientMapper,
                                UserRepository userRepository) {
        this.salesOrderService = salesOrderService;
        this.clientMapper = clientMapper;
        this.userRepository = userRepository;
    }

    /**
     * Vérifie l'ownership : un client ne peut accéder qu'à ses propres commandes
     */
    private void checkOwnership(SalesOrder order) {
        Optional<User> currentUser = SecurityUtils.getCurrentUser(userRepository);
        if (currentUser.isPresent() && currentUser.get().getRole() == Role.CLIENT) {
            Client client = (Client) currentUser.get();
            if (!order.getClient().getId().equals(client.getId())) {
                throw new AccessDeniedException("You can only access your own orders");
            }
        }
    }

    @GetMapping
    public ResponseEntity<List<SalesOrderDto>> list(
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) OrderStatus status) {
        // Si l'utilisateur est un client, forcer le filtrage par son ID
        Optional<UUID> currentClientId = SecurityUtils.getCurrentClientId(userRepository);
        if (currentClientId.isPresent()) {
            clientId = currentClientId.get();
        }
        
        List<SalesOrder> orders = salesOrderService.findAll(Optional.ofNullable(clientId), Optional.ofNullable(status));
        List<SalesOrderDto> dtos = orders.stream().map(o -> {
            java.util.List<com.example.digitallogistics.model.entity.SalesOrderLine> lines = salesOrderService.findLines(o.getId());
            return SalesOrderMapper.toDto(o, clientMapper, lines);
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<SalesOrderDto> create(@RequestBody @Valid SalesOrderCreateDto dto) {
        SalesOrder created = salesOrderService.create(dto);
        java.util.List<SalesOrderLine> lines = salesOrderService.findLines(created.getId());
        return ResponseEntity.ok(SalesOrderMapper.toDto(created, clientMapper, lines));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderDto> get(@PathVariable UUID id) {
        return salesOrderService.findById(id)
            .map(o -> {
                checkOwnership(o);
                java.util.List<SalesOrderLine> lines = salesOrderService.findLines(o.getId());
                return ResponseEntity.ok(SalesOrderMapper.toDto(o, clientMapper, lines));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reserve")
    public ResponseEntity<SalesOrderDto> reserve(@PathVariable UUID id) {
        SalesOrder order = salesOrderService.findById(id)
            .orElseThrow(() -> new com.example.digitallogistics.exception.ResourceNotFoundException("Order not found"));
        checkOwnership(order);
        SalesOrder updated = salesOrderService.reserve(id);
        java.util.List<SalesOrderLine> lines = salesOrderService.findLines(updated.getId());
        return ResponseEntity.ok(SalesOrderMapper.toDto(updated, clientMapper, lines));
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<SalesOrderDto> ship(@PathVariable UUID id) {
        SalesOrder order = salesOrderService.findById(id)
            .orElseThrow(() -> new com.example.digitallogistics.exception.ResourceNotFoundException("Order not found"));
        checkOwnership(order);
        SalesOrder updated = salesOrderService.ship(id);
        java.util.List<SalesOrderLine> lines = salesOrderService.findLines(updated.getId());
        return ResponseEntity.ok(SalesOrderMapper.toDto(updated, clientMapper, lines));
    }

    @PutMapping("/{id}/deliver")
    public ResponseEntity<SalesOrderDto> deliver(@PathVariable UUID id) {
        SalesOrder order = salesOrderService.findById(id)
            .orElseThrow(() -> new com.example.digitallogistics.exception.ResourceNotFoundException("Order not found"));
        checkOwnership(order);
        SalesOrder updated = salesOrderService.deliver(id);
        java.util.List<SalesOrderLine> lines = salesOrderService.findLines(updated.getId());
        return ResponseEntity.ok(SalesOrderMapper.toDto(updated, clientMapper, lines));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<SalesOrderDto> cancel(@PathVariable UUID id) {
        SalesOrder order = salesOrderService.findById(id)
            .orElseThrow(() -> new com.example.digitallogistics.exception.ResourceNotFoundException("Order not found"));
        checkOwnership(order);
        SalesOrder updated = salesOrderService.cancel(id);
        java.util.List<SalesOrderLine> lines = salesOrderService.findLines(updated.getId());
        return ResponseEntity.ok(SalesOrderMapper.toDto(updated, clientMapper, lines));
    }

}
