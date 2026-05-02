package com.n11bootcamp.product_service.repository;


import com.n11bootcamp.product_service.entity.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        Product product1 = new Product();
        product1.setTitle("Kazak");
        product1.setPrice(300L);
        product1.setCategory("Giyim");
        product1.setBrand("n11-Brand");
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setTitle("Pantolon");
        product2.setPrice(500L);
        product2.setCategory("Giyim");
        product2.setBrand("n11-Brand");
        productRepository.save(product2);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void shouldFindProductById() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setTitle("Gömlek");
        newProduct.setPrice(400L);
        newProduct.setCategory("Giyim"); // Buraya da ekle!
        newProduct.setBrand("n11-Brand");
        Product savedProduct = productRepository.save(newProduct);

        // Act
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals("Gömlek", foundProduct.get().getTitle());
    }

    @Test
    void shouldReturnPagedProducts() {
        Page<Product> page = productRepository.findAll(PageRequest.of(0, 1));

        // Assert
        assertNotNull(page);
        assertEquals(1, page.getContent().size()); // Sayfada 1 eleman olmalı
        assertEquals(2, page.getTotalElements());  // Toplamda 2 kayıt var (setUp'ta eklenen)
    }
}