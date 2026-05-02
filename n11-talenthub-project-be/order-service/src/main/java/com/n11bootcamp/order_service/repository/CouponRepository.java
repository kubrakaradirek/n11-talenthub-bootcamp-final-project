package com.n11bootcamp.order_service.repository;

import com.n11bootcamp.order_service.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCodeAndUserIdAndIsUsedFalse(String code, Long userId);

    List<Coupon> findByUserIdAndIsUsedFalse(Long userId);

    List<Coupon> findByUserIdInAndIsUsedFalse(List<Long> userIds);

    boolean existsByUserId(Long userId);
}
