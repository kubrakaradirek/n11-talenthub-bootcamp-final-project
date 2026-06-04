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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        sampleProduct.setTitle("Test Urunu");
        sampleProduct.setPrice(100L);
    }

    //findById Optional dönerse doğru Product döner mi?
    @Test
    void shouldGetProductByIdSuccess() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product found = productService.getProductById(1L);

        assertNotNull(found);
        assertEquals("Test Urunu", found.getTitle());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductById(99L));
    }

    @Test
    void shouldReturnPagedProductsSuccess() {
        Page<Product> expectedPage = new PageImpl<>(Collections.singletonList(sampleProduct));

        when(productRepository.findAll(any(PageRequest.class))).thenReturn(expectedPage);

        Page<Product> result = productService.getPagedProducts(0, 8);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}
