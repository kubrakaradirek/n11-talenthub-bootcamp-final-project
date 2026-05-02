package com.n11bootcamp.user_service.entity;

import java.util.Set;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Data
public class ShoppingCart {
    @Id
    private Long id;
    private String shoppingCartName;

    @ManyToMany(mappedBy = "products")
    private Set<ShoppingCart> shoppingCarts;

    public String getShoppingCartName() {
        return shoppingCartName;
    }

    public void setShoppingCartName(String shoppingCartName) {
        this.shoppingCartName = shoppingCartName;
    }

}
