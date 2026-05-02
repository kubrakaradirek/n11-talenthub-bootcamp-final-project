package com.n11bootcamp.product_service;

import com.n11bootcamp.product_service.entity.Product;
import com.n11bootcamp.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.junit.jupiter.api.Assertions.assertFalse;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc // API isteklerini simüle etmek için kullanılır.
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Sahte HTTP istekleri atar

    @Autowired
    private ProductRepository productRepository; // Gerçek veritabanı kontrolü için

    @BeforeEach
    void setup() {
        productRepository.deleteAll(); // Her testten önce veritabanını temizle
    }

    //Get istekleri herkese açık olduğu için token olmadan test
    @Test
    void shouldGetAllProductsSuccessfully() throws Exception {
        // Veritabanına test verisi ekle
        Product p = Product.builder().title("Entegrasyon Ürünü").price(200L).category("Test").build();
        productRepository.save(p);

        // API'ye istek at ve sonucu doğrula
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Entegrasyon Ürünü"));
    }

    //Post istekleri için test token gerekir
    @Test
    void shouldReturn401WhenCreatingProductWithoutToken() throws Exception {
        String productJson = """
        {
            "title": "Yeni Ürün",
            "price": 500,
            "category": "Elektronik"
        }
        """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isUnauthorized()); // 401 hatası bekliyoruz
    }

}