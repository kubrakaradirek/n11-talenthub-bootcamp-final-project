package com.n11bootcamp.stock_service.service;

import com.n11bootcamp.stock_service.entity.ProductStock;
import com.n11bootcamp.stock_service.repository.ProductStockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockInitDataRunner implements CommandLineRunner {

    private final ProductStockRepository repo;

    public StockInitDataRunner(ProductStockRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            List<ProductStock> stocks = List.of(
                    new ProductStock(1L, "iPhone 15 Pro", 50),
                    new ProductStock(2L, "Galaxy S24 Ultra", 30),
                    new ProductStock(3L, "Sony Bluetooth Kulaklık", 10),
                    new ProductStock(4L, "Dyson V15 Süpürge", 40),
                    new ProductStock(5L, "Philips Airfryer", 50),
                    new ProductStock(6L, "Logitech MX Master", 60),
                    new ProductStock(7L, "MacBook Air M3", 70),
                    new ProductStock(8L, "JBL Flip 6", 80),
                    new ProductStock(9L, "Bosch Kahve Makinesi", 25),
                    new ProductStock(10L, "Xiaomi Scooter", 15),
                    new ProductStock(11L, "Dell XPS 13", 20),
                    new ProductStock(12L, "Razer Klavye", 35),
                    new ProductStock(13L, "Canon Kamera", 8),
                    new ProductStock(14L, "Oral-B iO 9", 55),
                    new ProductStock(15L, "Nespresso Vertuo", 45),
                    new ProductStock(16L, "Nintendo Switch", 18),
                    new ProductStock(17L, "Tefal Ütü", 90),
                    new ProductStock(18L, "Samsung Smart TV", 22),
                    new ProductStock(19L, "HP LaserJet", 33),
                    new ProductStock(20L, "Kindle Paperwhite", 75),
                    new ProductStock(21L, "Roborock S8", 14),
                    new ProductStock(22L, "LG Monitör", 28),
                    new ProductStock(23L, "Siemens Bulaşık Makinesi", 12),
                    new ProductStock(24L, "Arçelik Çamaşır Makinesi", 10)
            );

            repo.saveAll(stocks);
        }
    }
}