package com.n11bootcamp.product_service.repository;

import com.n11bootcamp.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository  pagination (findAll(Pageable pageable)) desteği sunar.
}