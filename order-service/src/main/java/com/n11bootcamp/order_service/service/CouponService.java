package com.n11bootcamp.order_service.service;

import com.n11bootcamp.order_service.dto.CouponPreviewResponse;
import com.n11bootcamp.order_service.entity.Coupon;
import com.n11bootcamp.order_service.repository.CouponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CouponService {

    private static final Logger log = LoggerFactory.getLogger(CouponService.class);
    private static final double COUPON_LIMIT = 10000.0; // Kupon kazanma alt limiti
    private static final double DISCOUNT_RATE = 0.20; // %20 indirim

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponPreviewResponse previewCoupon(Long userId, String couponCode, Double totalPrice) {
        if (couponCode == null || couponCode.isBlank()) {
            log.info("Coupon preview skipped because coupon code is empty. userId={}", userId);
            return new CouponPreviewResponse(false, 0.0, totalPrice, "Kupon kodu girilmedi");
        }

        Coupon coupon = findValidCoupon(userId, couponCode); // Kupon kullanıcıya ait mi?
        double discountAmount = roundMoney(totalPrice * DISCOUNT_RATE);
        double discountedTotal = roundMoney(totalPrice - discountAmount);
        log.info("Coupon preview successful. userId={}, couponCode={}, discountAmount={}, discountedTotal={}",
                userId, coupon.getCode(), discountAmount, discountedTotal);
        return new CouponPreviewResponse(true, discountAmount, discountedTotal,
                "Kupon başarıyla uygulandı: %20 indirim");
    }

    public Double applyCouponIfPresent(Long userId, String couponCode, Double totalPrice) {
        if (couponCode == null || couponCode.isBlank()) {
            return totalPrice;
        }

        Coupon coupon = findValidCoupon(userId, couponCode);
        double discountAmount = roundMoney(totalPrice * DISCOUNT_RATE);
        double discountedTotal = roundMoney(totalPrice - discountAmount);
        coupon.setIsUsed(true); // Tekrar kullanılmasın
        couponRepository.save(coupon);
        log.info("Coupon used successfully. userId={}, couponCode={}, discountedTotal={}",
                userId, coupon.getCode(), discountedTotal);
        return discountedTotal;
    }

    public void createCouponForCompletedOrder(Long userId, Double totalPrice) {
        if (totalPrice == null || totalPrice < COUPON_LIMIT) {
            log.info("Coupon not created. userId={}, totalPrice={}, reason=below_limit", userId, totalPrice);
            return;
        }

        if (couponRepository.existsByUserId(userId)) { // Bir kullanıcı sadece bir kez kupon kazanır.
            log.info("Coupon not created. userId={}, reason=already_has_coupon_history", userId);
            return;
        }

        Coupon coupon = new Coupon();
        coupon.setCode("KUBA20-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        coupon.setUserId(userId); // Gerçek user id ile bağlıyoruz.
        coupon.setIsUsed(false);
        couponRepository.save(coupon);
        log.info("Coupon created for completed order. userId={}, couponCode={}, totalPrice={}",
                userId, coupon.getCode(), totalPrice);
    }

    public List<Coupon> findUnusedCoupons(Long userId) {
        return couponRepository.findByUserIdAndIsUsedFalse(userId);
    }

    private Coupon findValidCoupon(Long userId, String couponCode) {
        return couponRepository.findByCodeAndUserIdAndIsUsedFalse(couponCode.trim(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Kupon kodu geçersiz veya daha önce kullanılmış"));
    }

    private double roundMoney(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
}
