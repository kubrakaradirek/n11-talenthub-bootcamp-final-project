package com.n11bootcamp.shopping_card_service.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Card") // Veriler Redis'te "Card" altında tutuldu
public class ShoppingCard implements Serializable {

    @Id
    private String username; // Her usera bir sepet

    private List<CardItem> items = new ArrayList<>();
    private double totalPrice;

    public void calculateTotalPrice() {
        this.totalPrice = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}