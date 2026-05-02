package com.n11bootcamp.product_service.controller;

import com.n11bootcamp.product_service.entity.Product;
import com.n11bootcamp.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List products")
    public ResponseEntity<Page<Product>> getAllProducts(
            @Parameter(required = false) @RequestParam(defaultValue = "0") int page,
            @Parameter(required = false) @RequestParam(defaultValue = "8") int size) {
        return ResponseEntity.ok(productService.getPagedProducts(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id")
    public ResponseEntity<Product> getProductById(@Parameter(required = true) @PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
