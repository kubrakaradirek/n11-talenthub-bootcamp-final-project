package com.n11bootcamp.shopping_card_service.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardItem implements Serializable {
    private Long productId;
    private String title;
    private int quantity;
    private double price;
    private String imageUrl;
    private String color;
}
