package com.n11bootcamp.stock_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11bootcamp.stock_service.dto.StockUpdateRequest;
import com.n11bootcamp.stock_service.entity.ProductStock;
import com.n11bootcamp.stock_service.repository.ProductStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StockIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductStockRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long productId;

    @BeforeEach
    void setUp() {
        // Her testten önce veritabanını temizleme
        repository.deleteAll();

        // Veritabanına GERÇEK bir kayıt atılır
        productId = 1L;
        ProductStock stock = new ProductStock(productId, "Test MacBook Pro", 50);
        repository.save(stock);
    }

    // Helper Metot: Hızlıca JSON Request oluşturmak için
    private String createRequestJson(Long id, int quantity) throws Exception {
        StockUpdateRequest.StockItem item = new StockUpdateRequest.StockItem();
        item.setProductId(id);
        item.setQuantity(quantity);

        StockUpdateRequest request = new StockUpdateRequest();
        request.setItems(List.of(item));

        return objectMapper.writeValueAsString(request);
    }

    // ========================================================
    // RESERVE (REZERVASYON) ENTEGRASYON TESTLERİ
    // ========================================================

    @Test
    void shouldReserveStockAndReflectInDatabase_whenStockIsAvailable() throws Exception {
        // 50 stoğumuz var, 10 tanesini rezerve etmesi sağlanır
        String requestJson = createRequestJson(productId, 10);

        // 1. API'ye gerçek bir POST isteği atılır
        mockMvc.perform(post("/api/stocks/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Stock reserved"));

        // 2.Veritabanına gidip gerçekten değişmiş mi diye kontrol
        ProductStock updatedStock = repository.findById(productId).orElseThrow();
        assertEquals(40, updatedStock.getAvailableQuantity()); // 50 - 10 = 40 kalmalı
        assertEquals(10, updatedStock.getReservedQuantity());  // 10 rezerve olmalı
    }

    @Test
    void shouldFailToReserve_whenStockIsInsufficient() throws Exception {
        // 50 stok var ama 100 tane istenir=> (Hata almalı)
        String requestJson = createRequestJson(productId, 100);

        mockMvc.perform(post("/api/stocks/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk()) // Endpoint 200 dönüyor ama kendi içinde fail objesi dönme
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Insufficient stock for productId=" + productId));

        // Veritabanında hiçbir şeyin değişmediğini kontrolü
        ProductStock unchangedStock = repository.findById(productId).orElseThrow();
        assertEquals(50, unchangedStock.getAvailableQuantity());
        assertEquals(0, unchangedStock.getReservedQuantity());
    }

    // ========================================================
    // RELEASE (REZERVİ İPTAL ETME) ENTEGRASYON TESTİ
    // ========================================================

    @Test
    void shouldReleaseStockAndReflectInDatabase_whenPaymentFails() throws Exception {
        // Önce manuel olarak ürünü rezerve etme (Veritabanı durumu: Available=40, Reserved=10)
        ProductStock stock = repository.findById(productId).orElseThrow();
        stock.reserve(10);
        repository.save(stock);

        // 10 ürünü geri bırakmak (release) için istek atma
        String requestJson = createRequestJson(productId, 10);

        mockMvc.perform(post("/api/stocks/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Veritabanını kontrol et: Stoklar geri dönmüş mü?
        ProductStock updatedStock = repository.findById(productId).orElseThrow();
        assertEquals(50, updatedStock.getAvailableQuantity()); // Geri 50 oldu
        assertEquals(0, updatedStock.getReservedQuantity());   // Rezerve sıfırlandı
    }

    // ========================================================
    // COMMIT (SATIŞI ONAYLAMA) ENTEGRASYON TESTİ
    // ========================================================

    @Test
    void shouldCommitStockAndReflectInDatabase_whenPaymentSucceeds() throws Exception {
        // Önce manuel olarak ürünü rezerve etme (Veritabanı durumu: Available=40, Reserved=10)
        ProductStock stock = repository.findById(productId).orElseThrow();
        stock.reserve(10);
        repository.save(stock);

        // Satış onaylandı (commit), bu 10 ürün tamamen bizden çıkar
        String requestJson = createRequestJson(productId, 10);

        mockMvc.perform(post("/api/stocks/commit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Veritabanını kontrol et: Rezerve silinmiş mi? (Available zaten baştan düşmüştü)
        ProductStock updatedStock = repository.findById(productId).orElseThrow();
        assertEquals(40, updatedStock.getAvailableQuantity()); // Sabit kaldı
        assertEquals(0, updatedStock.getReservedQuantity());   // Rezerve silindi, ürün satıldı!
    }
}