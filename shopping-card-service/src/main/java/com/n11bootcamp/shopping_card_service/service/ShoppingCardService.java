package com.n11bootcamp.shopping_card_service.service;


import com.n11bootcamp.shopping_card_service.config.RabbitMQConfig;
import com.n11bootcamp.shopping_card_service.entity.CardItem;
import com.n11bootcamp.shopping_card_service.entity.ShoppingCard;
import com.n11bootcamp.shopping_card_service.repository.ShoppingCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // Loglama için Lombok anotasyonu
public class ShoppingCardService {

    private final ShoppingCardRepository cartRepository;
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    private static final String PRODUCT_SERVICE_URL = "http://PRODUCT-SERVICE/api/products/";

    // Sepeti getir, yoksa yeni oluştur
    public ShoppingCard getCart(String username) {
        return cartRepository.findById(username)
                .orElse(new ShoppingCard(username, new java.util.ArrayList<>(), 0.0));
    }

    // Sepete ürün ekle
    public ShoppingCard addToCart(String username, Long productId, int quantity) {
        ShoppingCard cart = getCart(username);

        // Product Service'den ürün detaylarını çekme
        try {
            Map productData = restTemplate.getForObject(PRODUCT_SERVICE_URL + productId, HashMap.class);
            if (productData == null) throw new RuntimeException("Product not found");

            double price = Double.parseDouble(productData.get("price").toString());
            String title = productData.get("title") != null ? productData.get("title").toString() : "Unknown Product";

            // Sepette bu ürün zaten varsa miktarını artır, yoksa yeni ekle
            Optional<CardItem> existingItem = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst();

            if (existingItem.isPresent()) {
                existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
            } else {
                cart.getItems().add(new CardItem(productId, title, quantity, price));
            }

            // Toplam fiyatı güncelle ve Redis'e kaydet
            cart.calculateTotalPrice();
            ShoppingCard savedCart = cartRepository.save(cart);

            // RabbitMQ'ya mesaj gönder (Stock servisi vb. için)
            String message = "Kullanıcı: " + username + " sepete ürün ekledi: " + title;
            rabbitTemplate.convertAndSend(RabbitMQConfig.CART_QUEUE, message);
            log.info("RabbitMQ'ya mesaj iletildi: {}", message);

            return savedCart;

        } catch (Exception e) {
            log.error("Ürün eklenirken hata oluştu: ", e);
            throw new RuntimeException("Ürün sepete eklenemedi.");
        }
    }

    // Sepeti tamamen boşalt
    public void clearCart(String username) {
        cartRepository.deleteById(username);
        log.info("{} kullanıcısının sepeti temizlendi.", username);
    }
}