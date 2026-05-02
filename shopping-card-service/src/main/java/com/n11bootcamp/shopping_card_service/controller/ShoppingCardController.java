package com.n11bootcamp.shopping_card_service.controller;

import com.n11bootcamp.shopping_card_service.entity.ShoppingCard;
import com.n11bootcamp.shopping_card_service.service.ShoppingCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shopping-cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Shopping Cart", description = "Authenticated shopping cart operations")
@SecurityRequirement(name = "bearerAuth")
public class ShoppingCardController {

    private final ShoppingCardService cartService;

    @GetMapping("/{username}")
    @Operation(summary = "Get cart", description = "Returns the authenticated user's cart.")
    public ResponseEntity<ShoppingCard> getCart(@Parameter(required = true) @PathVariable String username) {
        return ResponseEntity.ok(cartService.getCart(username));
    }

    @PostMapping("/{username}/add")
    @Operation(summary = "Add item to cart", description = "Adds a product to the authenticated user's cart.")
    public ResponseEntity<ShoppingCard> addToCart(
            @Parameter(required = true) @PathVariable String username,
            @Parameter(required = true) @RequestParam Long productId,
            @Parameter(required = true) @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(cartService.addToCart(username, productId, quantity));
    }

    @PostMapping("/{username}/update")
    @Operation(summary = "Update item quantity", description = "Updates a product quantity in the authenticated user's cart.")
    public ResponseEntity<ShoppingCard> updateQuantity(
            @Parameter(required = true) @PathVariable String username,
            @Parameter(required = true) @RequestParam Long productId,
            @Parameter(required = true) @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(username, productId, quantity));
    }

    @DeleteMapping("/{username}/remove")
    @Operation(summary = "Remove item from cart", description = "Removes a product from the authenticated user's cart.")
    public ResponseEntity<ShoppingCard> removeItem(
            @Parameter(required = true) @PathVariable String username,
            @Parameter(required = true) @RequestParam Long productId) {
        return ResponseEntity.ok(cartService.removeItem(username, productId));
    }

    @DeleteMapping("/{username}/clear")
    @Operation(summary = "Clear cart", description = "Clears the authenticated user's cart after a successful order.")
    public ResponseEntity<Void> clearCart(@Parameter(required = true) @PathVariable String username) {
        cartService.clearCart(username);
        return ResponseEntity.noContent().build();
    }
}
