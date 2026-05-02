package com.n11bootcamp.order_service.service;

import com.n11bootcamp.order_service.dto.CouponPreviewResponse;
import com.n11bootcamp.order_service.entity.Coupon;
import com.n11bootcamp.order_service.repository.CouponRepository;
import com.n11bootcamp.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CouponServiceTest {

    private final CouponRepository couponRepository = mock(CouponRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final CouponService couponService = new CouponService(couponRepository, orderRepository);

    @Test
    void previewCoupon_shouldCalculateTwentyPercentDiscount_whenCouponIsValid() {
        Coupon coupon = coupon("KUBA20-TEST01", 203L, false);
        when(couponRepository.findByCodeAndUserIdAndIsUsedFalse("KUBA20-TEST01", 203L))
                .thenReturn(Optional.of(coupon));

        CouponPreviewResponse response = couponService.previewCoupon(203L, "KUBA20-TEST01", 10000.0);

        assertThat(response.isValid()).isTrue();
        assertThat(response.getDiscountAmount()).isEqualTo(2000.0);
        assertThat(response.getDiscountedTotal()).isEqualTo(8000.0);
    }

    @Test
    void applyCouponIfPresent_shouldMarkCouponAsUsed_whenCouponIsValid() {
        Coupon coupon = coupon("KUBA20-TEST02", 203L, false);
        when(couponRepository.findByCodeAndUserIdAndIsUsedFalse("KUBA20-TEST02", 203L))
                .thenReturn(Optional.of(coupon));

        Double total = couponService.applyCouponIfPresent(203L, "KUBA20-TEST02", 5000.0);

        assertThat(total).isEqualTo(4000.0);
        assertThat(coupon.getIsUsed()).isTrue();
        verify(couponRepository).save(coupon);
    }

    @Test
    void createCouponForCompletedOrder_shouldCreateCouponOnlyForFirstOrderOverTenThousand() {
        when(couponRepository.existsByUserId(203L)).thenReturn(false);

        couponService.createCouponForCompletedOrder(203L, 10000.0);

        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void createCouponForCompletedOrder_shouldNotCreateCoupon_whenUserAlreadyHasCouponHistory() {
        when(couponRepository.existsByUserId(203L)).thenReturn(true);

        couponService.createCouponForCompletedOrder(203L, 15000.0);

        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void previewCoupon_shouldThrowException_whenCouponIsInvalid() {
        when(couponRepository.findByCodeAndUserIdAndIsUsedFalse("BADCODE", 203L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.previewCoupon(203L, "BADCODE", 10000.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Kupon kodu geçersiz veya daha önce kullanılmış");
    }

    private Coupon coupon(String code, Long userId, boolean isUsed) {
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setUserId(userId);
        coupon.setIsUsed(isUsed);
        return coupon;
    }
}
