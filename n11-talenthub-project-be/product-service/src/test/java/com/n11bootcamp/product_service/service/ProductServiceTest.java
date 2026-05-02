package com.n11bootcamp.product_service.service;

import com.n11bootcamp.product_service.entity.Product;
import com.n11bootcamp.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setTitle("Test Ürünü");
        sampleProduct.setPrice(100L);
    }

    @Test
    void shouldGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product found = productService.getProductById(1L);

        assertNotNull(found);
        assertEquals("Test Ürünü", found.getTitle());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductById(99L));
    }

    @Test
    void shouldCreateProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product savedProduct = productService.createProduct(sampleProduct);

        assertNotNull(savedProduct);
        assertEquals(1L, savedProduct.getId());
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    void shouldDeleteProduct_Success() {
        Long testId = 1L;
        when(productRepository.existsById(testId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(testId);

        assertDoesNotThrow(() -> productService.deleteProduct(testId));

        verify(productRepository, times(1)).existsById(testId);
        verify(productRepository, times(1)).deleteById(testId);
    }

    @Test
    void shouldReturnPagedProducts_Success() {
        int page = 0;
        int size = 8;
        Page<Product> expectedPage = new PageImpl<>(Collections.singletonList(sampleProduct));

        when(productRepository.findAll(any(PageRequest.class))).thenReturn(expectedPage);

        Page<Product> result = productService.getPagedProducts(page, size);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}