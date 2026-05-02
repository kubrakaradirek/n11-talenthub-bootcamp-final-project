package com.n11bootcamp.order_service.repository;

import com.n11bootcamp.order_service.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCodeAndUserIdAndIsUsedFalse(String code, Long userId); // Kullanılmamış ve kullanıcıya ait kuponu buluyoruz.

    List<Coupon> findByUserIdAndIsUsedFalse(Long userId); // Kullanıcının harcamadığı kuponları listeliyoruz.

    boolean existsByUserId(Long userId); // Kullanıcıya daha önce kupon tanımlanmış mı kontrol ediyoruz.
}
