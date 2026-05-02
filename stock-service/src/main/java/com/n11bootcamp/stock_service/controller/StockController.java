package com.n11bootcamp.stock_service.controller;

import com.n11bootcamp.stock_service.dto.StockUpdateRequest;
import com.n11bootcamp.stock_service.dto.StockUpdateResponse;
import com.n11bootcamp.stock_service.service.StockDomainService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
@Tag(name = "Stocks", description = "Authenticated stock reservation operations")
@SecurityRequirement(name = "bearerAuth")
public class StockController {

    private final StockDomainService stock;

    public StockController(StockDomainService stock) {
        this.stock = stock;
    }

    @Hidden
    @PostMapping("/decrease")
    public ResponseEntity<StockUpdateResponse> decrease(@RequestBody StockUpdateRequest req) {
        return ResponseEntity.ok(stock.decrease(req));
    }

    @Hidden
    @PostMapping("/increase")
    public ResponseEntity<StockUpdateResponse> increase(@RequestBody StockUpdateRequest req) {
        return ResponseEntity.ok(stock.increase(req));
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve stock", description = "Locks stock for a valid authenticated order flow.")
    public ResponseEntity<StockUpdateResponse> reserve(@RequestBody StockUpdateRequest req) {
        return ResponseEntity.ok(stock.reserve(req));
    }

    @PostMapping("/release")
    @Operation(summary = "Release reserved stock", description = "Releases previously reserved stock when checkout is cancelled or payment fails.")
    public ResponseEntity<StockUpdateResponse> release(@RequestBody StockUpdateRequest req) {
        return ResponseEntity.ok(stock.release(req));
    }

    @PostMapping("/commit")
    @Operation(summary = "Commit reserved stock", description = "Finalizes reserved stock after successful payment.")
    public ResponseEntity<StockUpdateResponse> commit(@RequestBody StockUpdateRequest req) {
        return ResponseEntity.ok(stock.commit(req));
    }
}
