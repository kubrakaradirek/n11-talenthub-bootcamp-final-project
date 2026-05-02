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

    private static final String PRODUCT_SERVICE_URL = "http://product-service:8764/api/products/";

    // Sepeti getir, yoksa yeni oluştur
    public ShoppingCard getCart(String username) {
        return cartRepository.findById(username)
                .orElse(new ShoppingCard(username, new java.util.ArrayList<>(), 0.0));
    }

    // Sepete ürün ekle
    public ShoppingCard addToCart(String username, Long productId, int quantity) {
        ShoppingCard cart = getCart(username);

        try {
            // 1. Product Service'den tüm veriyi çekiyorsun (HARİKA KISIM BURASI)
            Map productData = restTemplate.getForObject(PRODUCT_SERVICE_URL + productId, HashMap.class);
            if (productData == null) throw new RuntimeException("Product not found");

            // 2. Gelen veriden ihtiyacın olanları ayrıştır
            double price = Double.parseDouble(productData.get("price").toString());
            String title = productData.get("title") != null ? productData.get("title").toString() : "Unknown Product";

            // YENİ EKLENEN: Resim ve Rengi de RestTemplate'den gelen datadan al!
            String imageUrl = productData.get("img") != null ? productData.get("img").toString() : "";
            // Eğer Product servisinde color yoksa şimdilik "Standart" basıyoruz.
            String color = productData.get("color") != null ? productData.get("color").toString() : "Standart";

            // 3. Sepette bu ürün zaten varsa miktarını artır
            Optional<CardItem> existingItem = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst();

            if (existingItem.isPresent()) {
                existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
            } else {
                // YENİ EKLENEN: CardItem oluştururken imageUrl ve color da gönder!
                cart.getItems().add(new CardItem(productId, title, quantity, price, imageUrl, color));
            }

            // Toplam fiyatı güncelle ve Redis'e kaydet
            cart.calculateTotalPrice();
            ShoppingCard savedCart = cartRepository.save(cart);

            // RabbitMQ'ya mesaj gönder
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
    public ShoppingCard updateQuantity(String username, Long productId, int quantity) {
        // 1. Mevcut sepeti getir
        ShoppingCard cart = getCart(username);

        // 2. Sepetteki ilgili ürünü bul ve miktarını güncelle
        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        // 3. Toplam fiyatı yeniden hesapla
        recalculateTotalPrice(cart);

        // 4. Güncel sepeti Redis'e/DB'ye geri kaydet
        return cartRepository.save(cart);
    }
    // oplam Fiyatı Yeniden Hesaplayan Metot
    private void recalculateTotalPrice(ShoppingCard cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }

    public ShoppingCard removeItem(String username, Long productId) {
        ShoppingCard cart = getCart(username);

        // Ürünü listeden productId'ye göre bul ve çıkar
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        // Toplam fiyatı tekrar hesapla
        recalculateTotalPrice(cart);

        // Güncel sepeti kaydet ve dön
        return cartRepository.save(cart);
    }
}
