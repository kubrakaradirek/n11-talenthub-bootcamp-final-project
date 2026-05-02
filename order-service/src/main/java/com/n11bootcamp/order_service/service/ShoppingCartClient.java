package com.n11bootcamp.order_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shopping-cart-service", path = "/api/shopping-cart")
public interface ShoppingCartClient {

    @DeleteMapping("/{username}/clear")
    void clearCart(@PathVariable String username);
}
