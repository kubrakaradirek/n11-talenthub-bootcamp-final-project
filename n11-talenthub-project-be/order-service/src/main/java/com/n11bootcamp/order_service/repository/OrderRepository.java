package com.n11bootcamp.order_service.repository;

import com.n11bootcamp.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUsername(String username);

    @Query("select distinct o.userId from Order o where lower(o.username) = lower(:username) and o.userId is not null")
    List<Long> findDistinctUserIdsByUsernameIgnoreCase(@Param("username") String username);
}
