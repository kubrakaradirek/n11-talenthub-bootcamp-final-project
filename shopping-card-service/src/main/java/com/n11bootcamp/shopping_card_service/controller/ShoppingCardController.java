package com.n11bootcamp.shopping_card_service.controller;


import com.n11bootcamp.shopping_card_service.entity.ShoppingCard;
import com.n11bootcamp.shopping_card_service.service.ShoppingCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping-cart")
@RequiredArgsConstructor
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

    @DeleteMapping("/{username}/clear")
    public ResponseEntity<String> clearCart(@PathVariable String username) {
        cartService.clearCart(username);
        return ResponseEntity.ok("Sepet temizlendi.");
    }
}