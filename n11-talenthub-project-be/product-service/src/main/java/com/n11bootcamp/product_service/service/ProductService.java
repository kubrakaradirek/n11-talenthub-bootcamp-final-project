package com.n11bootcamp.product_service.service;

import com.n11bootcamp.product_service.dto.ProductRequest;
import com.n11bootcamp.product_service.entity.Product;
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
@Slf4j // Lombok'un loglama anotasyonu
public class ProductService {

    private final ProductRepository productRepository;

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Ürün bulunamadı! Aranan ID: {}", id);
                    return new RuntimeException("Ürün bulunamadı!");
                });
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .title(productRequest.getTitle())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .img(productRequest.getImg())
                .brand(productRequest.getBrand())
                .color(productRequest.getColor())
                .category(productRequest.getCategory())
                .build();

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductRequest updatedProduct) {
        Product existingProduct = getProductById(id);

        existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setImg(updatedProduct.getImg());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setColor(updatedProduct.getColor());
        existingProduct.setCategory(updatedProduct.getCategory());

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Silinecek ürün bulunamadı!");
        }
        productRepository.deleteById(id);
    }

    // Pagination (Sayfalama)
    public Page<Product> getPagedProducts(int page, int size) {
        log.info("Ürün listesi sayfalama ile çekiliyor. Sayfa: {}, Boyut: {}", page, size);
        return productRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        );
    }

}
