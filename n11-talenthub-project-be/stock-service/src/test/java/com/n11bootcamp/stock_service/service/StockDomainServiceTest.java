package com.n11bootcamp.stock_service.service;

import com.n11bootcamp.stock_service.dto.StockUpdateRequest;
import com.n11bootcamp.stock_service.dto.StockUpdateResponse;
import com.n11bootcamp.stock_service.entity.ProductStock;
import com.n11bootcamp.stock_service.repository.ProductStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockDomainServiceTest {

    @Mock
    private ProductStockRepository repo;

    @InjectMocks
    private StockDomainService service;

    private Long productId;
    private ProductStock mockStock;

    @BeforeEach
    void setUp() {
        productId = 1L;
        // Testlere başlamadan önce standart bir ürün oluşturma. (50 adet stok)
        mockStock = new ProductStock(productId, "Test Ürünü", 50);
    }

    // Helper Metot: Testlerde tekrar tekrar Request oluşturmamak için
    private StockUpdateRequest createRequest(Long id, int quantity) {
        StockUpdateRequest.StockItem item = new StockUpdateRequest.StockItem();
        item.setProductId(id);
        item.setQuantity(quantity);

        StockUpdateRequest request = new StockUpdateRequest();
        request.setItems(List.of(item));
        return request;
    }

    // ==========================================
    // RESERVE (REZERVE ETME) TESTLERİ
    // ==========================================

    @Test
    void reserve_shouldReturnOk_whenStockIsSufficient() {
        // Given
        StockUpdateRequest request = createRequest(productId, 5);
        when(repo.findById(productId)).thenReturn(Optional.of(mockStock));

        // When
        StockUpdateResponse response = service.reserve(request);

        // Then
        assertEquals("Stock reserved", response.getMessage());
        // Serviste for döngüsü iki kez döndüğü için findById iki kez çağrılıyor
        verify(repo, times(2)).findById(productId);
        verify(repo, times(1)).save(mockStock);
    }

    @Test
    void reserve_shouldReturnFail_whenStockIsInsufficient() {
        // Given
        StockUpdateRequest request = createRequest(productId, 100); // 50 stok var, 100 istendi
        when(repo.findById(productId)).thenReturn(Optional.of(mockStock));

        // When
        StockUpdateResponse response = service.reserve(request);

        // Then
        assertTrue(response.getMessage().contains("Insufficient stock"));
        verify(repo, times(1)).findById(productId); // Hata ilk döngüde fırladığı için 1 kez çağrılır
        verify(repo, never()).save(any()); // Save işlemi ASLA çalışmamalı
    }

    // ==========================================
    // RELEASE (REZERVASYONU GERİ BIRAKMA) TESTLERİ
    // ==========================================

    @Test
    void release_shouldReturnOk_whenReservedStockIsSufficient() {
        // Given
        mockStock.reserve(10); // Önce 10 tane rezerve edilmiş gibi durumu ayarlama
        StockUpdateRequest request = createRequest(productId, 5); // 5 tanesini geri bırakma
        when(repo.findById(productId)).thenReturn(Optional.of(mockStock));

        // When
        StockUpdateResponse response = service.release(request);

        // Then
        assertEquals("Stock released", response.getMessage());
        verify(repo, times(2)).findById(productId);
        verify(repo, times(1)).save(mockStock);
    }

    @Test
    void release_shouldReturnFail_whenReservedStockIsInsufficient() {
        // Given
        mockStock.reserve(2); // Sadece 2 rezerve var
        StockUpdateRequest request = createRequest(productId, 10); // Ama 10 tane bırakmaya çalışma
        when(repo.findById(productId)).thenReturn(Optional.of(mockStock));

        // When
        StockUpdateResponse response = service.release(request);

        // Then
        assertTrue(response.getMessage().contains("Insufficient reserved stock"));
        verify(repo, never()).save(any());
    }

    // ==========================================
    // (SATIŞI KESİNLEŞTİRME) TESTLERİ
    // ==========================================

    @Test
    void commit_shouldReturnOk_whenReservedStockIsSufficient() {
        // Given
        mockStock.reserve(10); // 10 tane önceden rezerve edilmiş
        StockUpdateRequest request = createRequest(productId, 10); // Bu 10 taneyi satma
        when(repo.findById(productId)).thenReturn(Optional.of(mockStock));

        // When
        StockUpdateResponse response = service.commit(request);

        // Then
        assertEquals("Stock committed", response.getMessage());
        verify(repo, times(2)).findById(productId);
        verify(repo, times(1)).save(mockStock);
    }

    // ==========================================
    // DECREASE (ESKİ YAPI - DİREKT DÜŞÜRME) TESTİ
    // ==========================================

    @Test
    void decrease_shouldReturnOk_whenStockIsSufficient() {
        // Given
        StockUpdateRequest request = createRequest(productId, 10);
        when(repo.findById(productId)).thenReturn(Optional.of(mockStock));

        // When
        StockUpdateResponse response = service.decrease(request);

        // Then
        assertEquals("Stock decreased", response.getMessage());
        verify(repo, times(2)).findById(productId);
        verify(repo, times(1)).save(mockStock);
    }

    // ==========================================
    //  INCREASE (ESKİ YAPI - DİREKT ARTIRMA) TESTİ
    // ==========================================

    @Test
    void increase_shouldReturnOk_whenProductExists() {
        // Given
        StockUpdateRequest request = createRequest(productId, 20);
        when(repo.findById(productId)).thenReturn(Optional.of(mockStock));

        // When
        StockUpdateResponse response = service.increase(request);

        // Then
        assertEquals("Stock increased", response.getMessage());
        verify(repo, times(1)).findById(productId); // increase metodunda sadece 1 for döngüsü var
        verify(repo, times(1)).save(mockStock);
    }

    // ==========================================
    // GENEL HATA (PRODUCT NOT FOUND) TESTİ
    // ==========================================

    @Test
    void shouldReturnFail_whenProductDoesNotExist() {
        // Given
        StockUpdateRequest request = createRequest(999L, 5); // Veritabanında olmayan ID
        when(repo.findById(999L)).thenReturn(Optional.empty()); // Boş döndür

        // When
        StockUpdateResponse response = service.reserve(request);

        // Then
        assertTrue(response.getMessage().contains("Product not found"));
        verify(repo, never()).save(any());
    }
}