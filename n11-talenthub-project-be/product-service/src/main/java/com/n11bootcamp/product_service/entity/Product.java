package com.n11bootcamp.product_service.entity;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data // Getter, Setter, toString gibi metodları otomatik üretir.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private long price;

    @Column(length = 512)
    private String img; // Resim URL'sini frontend veya AI buraya string olarak iletrir.

    @Column(length = 255)
    private String brand;

    @Column(length = 100)
    private String color;

    @Column(nullable = false, length = 255)
    private String category;
}
