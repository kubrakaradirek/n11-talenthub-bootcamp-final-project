package com.n11bootcamp.shopping_card_service;

import com.n11bootcamp.shopping_card_service.entity.ShoppingCard;
import com.n11bootcamp.shopping_card_service.repository.ShoppingCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ShoppingCardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShoppingCardRepository cartRepository;

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setup() {
        // Her testten önce Redis'teki test verilerini temizleme
        cartRepository.deleteAll();
    }

    @Test
    void shouldGetCartSuccessfully() throws Exception {
        // Redis'e örnek bir sepet kaydetme
        ShoppingCard cart = new ShoppingCard("kubra", new ArrayList<>(), 0.0);
        cartRepository.save(cart);

        // API'ye GET isteği atma
        mockMvc.perform(get("/api/shopping-cart/kubra"))
                // .with(jwt()) // Güvenlik varsa yorum satırını kaldır
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("kubra"))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void shouldAddToCartSuccessfully() throws Exception {
        // RestTemplate'in döneceği sahte ürün veri hazırlığı
        HashMap<String, Object> mockProductResponse = new HashMap<>();
        mockProductResponse.put("id", 101L);
        mockProductResponse.put("title", "Integration Test Ürünü");
        mockProductResponse.put("price", 150.0);

        when(restTemplate.getForObject(anyString(), eq(HashMap.class))).thenReturn(mockProductResponse);

        // API'ye POST isteği atma
        mockMvc.perform(post("/api/shopping-cart/kubra/add")
                        .param("productId", "101")
                        .param("quantity", "2"))
                // .with(jwt()) // Güvenlik varsa yorum satırını kaldır
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("kubra"))
                .andExpect(jsonPath("$.items[0].productId").value(101))
                .andExpect(jsonPath("$.items[0].title").value("Integration Test Ürünü"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(300.0)); // 150 * 2 = 300

        // RabbitMQ kuyruğuna mesaj gönderildiğini doğrulayalım
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString());
    }

    @Test
    void shouldClearCartSuccessfully() throws Exception {
        // Önce Redis'e içi dolu bir sepet ekleme
        ShoppingCard cart = new ShoppingCard("kubra", new ArrayList<>(), 0.0);
        cartRepository.save(cart);

        // API'den temizleme delete isteği gönderme
        mockMvc.perform(delete("/api/shopping-cart/kubra/clear"))
                // .with(jwt()) // Güvenlik varsa yorum satırını kaldır
                .andExpect(status().isOk())
                .andExpect(content().string("Sepet temizlendi."));

        // Veritabanından gerçekten silindi mi diye kontrol etme
        boolean exists = cartRepository.findById("kubra").isPresent();
        assertFalse(exists, "Sepet Redis'ten silinmiş olmalı!");
    }

    //Var olan ürün tekrar eklendiğinde miktar ve toplam fiyatının doğru şekilde artıp artmaması
    @Test
    void shouldUpdateCartItemQuantity() throws Exception {
        // Önce Redis'e içinde 1 adet "101" ID'li ürün olan bir sepet kaydetme
        ShoppingCard cart = new ShoppingCard("kubra", new ArrayList<>(), 0.0);
        cart.getItems().add(new com.n11bootcamp.shopping_card_service.entity.CardItem(101L, "Integration Test Ürünü", 1, 150.0));
        cart.calculateTotalPrice(); // Başlangıç fiyatı 150.0
        cartRepository.save(cart);

        // RestTemplate'in döneceği sahte ürün verisini yine hazırlığı
        HashMap<String, Object> mockProductResponse = new HashMap<>();
        mockProductResponse.put("id", 101L);
        mockProductResponse.put("title", "Integration Test Ürünü");
        mockProductResponse.put("price", 150.0);

        when(restTemplate.getForObject(anyString(), eq(HashMap.class))).thenReturn(mockProductResponse);

        // API'ye istek atıp AYNI üründen 3 tane daha ekleme
        mockMvc.perform(post("/api/shopping-cart/kubra/add")
                        .param("productId", "101")
                        .param("quantity", "3"))
                // .with(jwt()) // Güvenlik varsa bu yorum satırını kaldır
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("kubra"))
                .andExpect(jsonPath("$.items[0].quantity").value(4))
                .andExpect(jsonPath("$.totalPrice").value(600.0));

        // RabbitMQ kuyruğuna güncelleme için de mesaj gittiğini doğrulama
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString());
    }
}