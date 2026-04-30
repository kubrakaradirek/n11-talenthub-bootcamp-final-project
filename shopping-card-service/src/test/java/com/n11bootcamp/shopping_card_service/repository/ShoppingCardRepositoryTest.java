package com.n11bootcamp.shopping_card_service.repository;


import com.n11bootcamp.shopping_card_service.entity.ShoppingCard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShoppingCardRepositoryTest {

    @Autowired
    private ShoppingCardRepository cartRepository;

    @BeforeEach
    void setUp() {
        ShoppingCard cart = new ShoppingCard("kubra", new ArrayList<>(), 500.0);
        cartRepository.save(cart);
    }

    @AfterEach
    void tearDown() {
        cartRepository.deleteById("kubra");
    }

    @Test
    void shouldSaveAndFindCartById() {
        Optional<ShoppingCard> foundCart = cartRepository.findById("kubra");

        assertTrue(foundCart.isPresent());
        assertEquals(500.0, foundCart.get().getTotalPrice());
    }

    @Test
    void shouldDeleteCartById() {
        cartRepository.deleteById("kubra");

        Optional<ShoppingCard> foundCart = cartRepository.findById("kubra");
        assertFalse(foundCart.isPresent());
    }
}