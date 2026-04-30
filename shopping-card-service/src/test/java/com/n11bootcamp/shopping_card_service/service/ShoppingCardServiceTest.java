package com.n11bootcamp.shopping_card_service.service;


import com.n11bootcamp.shopping_card_service.config.RabbitMQConfig;
import com.n11bootcamp.shopping_card_service.entity.ShoppingCard;
import com.n11bootcamp.shopping_card_service.repository.ShoppingCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCardServiceTest {

    @Mock
    private ShoppingCardRepository cartRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ShoppingCardService cartService;

    private ShoppingCard sampleCart;

    @BeforeEach
    void setUp() {
        sampleCart = new ShoppingCard("kubra", new ArrayList<>(), 0.0);
    }

    @Test
    void shouldReturnExistingCart_WhenCartExists() {
        when(cartRepository.findById("kubra")).thenReturn(Optional.of(sampleCart));

        ShoppingCard cart = cartService.getCart("kubra");

        assertNotNull(cart);
        assertEquals("kubra", cart.getUsername());
        verify(cartRepository, times(1)).findById("kubra");
    }

    @Test
    void shouldAddToCart_Success() {
        // Arrange
        Long productId = 1L;
        int quantity = 2;
        Map<String, Object> mockProductData = new HashMap<>();
        mockProductData.put("title", "Test Bilgisayarı");
        mockProductData.put("price", 15000.0);

        when(cartRepository.findById("kubra")).thenReturn(Optional.of(sampleCart));
        doReturn(mockProductData).when(restTemplate).getForObject(anyString(), eq(HashMap.class));
        when(cartRepository.save(any(ShoppingCard.class))).thenReturn(sampleCart);

        // Act
        ShoppingCard updatedCart = cartService.addToCart("kubra", productId, quantity);

        // Assert
        assertNotNull(updatedCart);
        verify(cartRepository).save(any(ShoppingCard.class));
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.CART_QUEUE), anyString());
    }

    @Test
    void shouldThrowException_WhenProductNotFoundInAddToCart() {
        when(cartRepository.findById("kubra")).thenReturn(Optional.of(sampleCart));
        when(restTemplate.getForObject(anyString(), eq(HashMap.class))).thenReturn(null);

        assertThrows(RuntimeException.class, () -> cartService.addToCart("kubra", 99L, 1));

        verify(cartRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString());
    }

    @Test
    void shouldClearCart_Success() {
        doNothing().when(cartRepository).deleteById("kubra");

        assertDoesNotThrow(() -> cartService.clearCart("kubra"));

        verify(cartRepository, times(1)).deleteById("kubra");
    }
}