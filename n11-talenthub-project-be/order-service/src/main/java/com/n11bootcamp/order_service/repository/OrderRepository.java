package com.n11bootcamp.order_service.repository;

import com.n11bootcamp.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUsername(String username);

    List<Order> findByUsernameIgnoreCaseAndUserIdIsNotNull(String username);
}
