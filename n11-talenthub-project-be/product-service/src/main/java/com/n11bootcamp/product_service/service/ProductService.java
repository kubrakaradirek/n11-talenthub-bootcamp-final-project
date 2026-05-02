package com.n11bootcamp.product_service.service;

import com.n11bootcamp.product_service.entity.Product;
import com.n11bootcamp.product_service.exception.InvalidProductPageException;
import com.n11bootcamp.product_service.exception.ProductNotFoundException;
import com.n11bootcamp.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Urun bulunamadi. Aranan ID: {}", id);
                    return new ProductNotFoundException("Boyle bir urun yok.");
                });
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getPagedProducts(int page, int size) {
        if (page < 0 || size < 1) {
            throw new InvalidProductPageException("Gecersiz page veya size degeri.");
        }

        log.info("Urun listesi sayfalama ile cekiliyor. Sayfa: {}, Boyut: {}", page, size);
        return productRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        );
    }
}
