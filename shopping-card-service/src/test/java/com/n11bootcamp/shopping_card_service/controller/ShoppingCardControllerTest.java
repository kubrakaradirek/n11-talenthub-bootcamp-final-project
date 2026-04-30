package com.n11bootcamp.shopping_card_service.controller;

import com.n11bootcamp.shopping_card_service.entity.ShoppingCard;
import com.n11bootcamp.shopping_card_service.service.ShoppingCardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShoppingCardController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShoppingCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShoppingCardService cartService;

    private ShoppingCard sampleCart;

    @BeforeEach
    void setUp() {
        sampleCart = new ShoppingCard("kubra", new ArrayList<>(), 250.0);
    }

    @Test
    void shouldGetCart() throws Exception {
        when(cartService.getCart("kubra")).thenReturn(sampleCart);

        mockMvc.perform(get("/api/shopping-cart/kubra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("kubra"))
                .andExpect(jsonPath("$.totalPrice").value(250.0));
    }

    @Test
    void shouldAddToCart() throws Exception {
        when(cartService.addToCart("kubra", 1L, 2)).thenReturn(sampleCart);

        mockMvc.perform(post("/api/shopping-cart/kubra/add")
                        .param("productId", "1")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("kubra"));
    }

    @Test
    void shouldClearCart() throws Exception {
        doNothing().when(cartService).clearCart("kubra");

        mockMvc.perform(delete("/api/shopping-cart/kubra/clear"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sepet temizlendi."));
    }
}