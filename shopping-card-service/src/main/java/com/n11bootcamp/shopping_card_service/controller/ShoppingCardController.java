package com.n11bootcamp.shopping_card_service.controller;


import com.n11bootcamp.shopping_card_service.entity.ShoppingCard;
import com.n11bootcamp.shopping_card_service.service.ShoppingCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin; // Bunu import etmeyi unutma

@RestController
@RequestMapping("/api/shopping-cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class ShoppingCardController {

    private final ShoppingCardService cartService;

    @GetMapping("/{username}")
    public ResponseEntity<ShoppingCard> getCart(@PathVariable String username) {
        return ResponseEntity.ok(cartService.getCart(username));
    }

    @PostMapping("/{username}/add")
    public ResponseEntity<ShoppingCard> addToCart(
            @PathVariable String username,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(cartService.addToCart(username, productId, quantity));
    }
    @PostMapping("/{username}/update")
    public ResponseEntity<ShoppingCard> updateQuantity(
            @PathVariable String username,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        // cartService
        return ResponseEntity.ok(cartService.updateQuantity(username, productId, quantity));
    }
    @DeleteMapping("/{username}/remove")
    public ResponseEntity<ShoppingCard> removeItem(
            @PathVariable String username,
            @RequestParam Long productId) {
        return ResponseEntity.ok(cartService.removeItem(username, productId));
    }
}