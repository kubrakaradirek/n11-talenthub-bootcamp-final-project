package com.n11bootcamp.product_service.controller;

import com.n11bootcamp.product_service.entity.Product;
import com.n11bootcamp.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create product", description = "Yeni bir urun ekler.")
    public ResponseEntity<Product> createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true)
            @RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Secilen urunun bilgilerini gunceller.")
    public ResponseEntity<Product> updateProduct(
            @Parameter(required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true)
            @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Secilen urunu siler.")
    public ResponseEntity<String> deleteProduct(@Parameter(required = true) @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully.");
    }

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
