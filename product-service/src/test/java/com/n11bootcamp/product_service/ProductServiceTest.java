package com.n11bootcamp.product_service;

import com.n11bootcamp.product_service.entity.Product;
import com.n11bootcamp.product_service.repository.ProductRepository;
import com.n11bootcamp.product_service.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito'yu devreye girer.
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository; // Sahte depo

    @InjectMocks
    private ProductService productService; // Sahte deponun enjekte edileceği gerçek servis

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setTitle("Test Ürünü");
        sampleProduct.setPrice(100L);
    }

    @Test
    void getProductById_Success() {
        // Arrange: Repository 1L id'li ürünü döndürsün
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        // Servis çağırılır
        Product found = productService.getProductById(1L);

        // Doğrulama
        assertNotNull(found);
        assertEquals("Test Ürünü", found.getTitle());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_NotFound_ShouldThrowException() {
        // Boş bir sonuç döner
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Exception fırlatması beklenir
        assertThrows(RuntimeException.class, () -> productService.getProductById(99L));
    }
}