package com.example.digitallogistics.controller;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.model.dto.ProductCreateDto;
import com.example.digitallogistics.model.dto.ProductDto;
import com.example.digitallogistics.model.dto.ProductUpdateDto;
import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.model.mapper.ProductMapper;
import com.example.digitallogistics.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @SuppressWarnings("null")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public Page<ProductDto> list(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size,
                                 @RequestParam(required = false) String search,
                                 @RequestParam(required = false) Boolean active) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAll(pageable, search, active);
        return new PageImpl<>(products.stream().map(productMapper::toDto).collect(Collectors.toList()), pageable,
                products.getTotalElements());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<ProductDto> get(@PathVariable UUID id) {
        Optional<Product> opt = productService.findById(id);
        return opt.map(p -> ResponseEntity.ok(productMapper.toDto(p))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<ProductDto> searchBySku(@RequestParam String sku) {
        Optional<Product> opt = productService.findBySku(sku);
        return opt.map(p -> ResponseEntity.ok(productMapper.toDto(p))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @SuppressWarnings("null")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<ProductDto> create(@RequestBody @Valid ProductCreateDto createDto) {
        Product product = productMapper.toEntity(createDto);
        Product saved = productService.create(product);
        ProductDto dto = productMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/products/" + dto.getId())).body(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<ProductDto> update(@PathVariable UUID id, @RequestBody @Valid ProductUpdateDto updateDto) {
        Optional<Product> existing = productService.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();

        Product product = existing.get();
        productMapper.updateFromDto(updateDto, product);
        Optional<Product> updated = productService.update(id, product);
        return updated.map(p -> ResponseEntity.ok(productMapper.toDto(p))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<ProductDto> activateToggle(@PathVariable UUID id) {
        Optional<Product> opt = productService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Product p = opt.get();
        p.setActive(!p.isActive());
        productService.update(id, p);
        return ResponseEntity.ok(productMapper.toDto(p));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        Optional<Product> opt = productService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{sku}/desactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> desactivate(@PathVariable String sku){
        Optional<Product> optProduct = productService.findBySku(sku);
        if (optProduct.isEmpty()) return ResponseEntity.notFound().build();
        if (!productService.desactivate(sku)) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        Product product = optProduct.get();
        if (product.isActive()) product.setActive(false);
        productService.update(product.getId(), product);
        return ResponseEntity.ok(productMapper.toDto(product));
    }
}
