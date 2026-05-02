package com.n11bootcamp.order_service.controller;

import com.n11bootcamp.order_service.dto.CouponPreviewResponse;
import com.n11bootcamp.order_service.entity.Coupon;
import com.n11bootcamp.order_service.service.CouponService;
import com.n11bootcamp.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerCouponTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CouponService couponService;

    @Test
    void previewCoupon_shouldReturnDiscountInformation() throws Exception {
        when(couponService.previewCoupon(203L, "KUBA20-TEST01", 10000.0))
                .thenReturn(new CouponPreviewResponse(true, 2000.0, 8000.0,
                        "Kupon başarıyla uygulandı: %20 indirim"));

        mockMvc.perform(get("/api/orders/coupons/preview")
                        .param("userId", "203")
                        .param("couponCode", "KUBA20-TEST01")
                        .param("totalPrice", "10000.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.discountAmount").value(2000.0))
                .andExpect(jsonPath("$.discountedTotal").value(8000.0));
    }

    @Test
    void getUnusedCoupons_shouldReturnActiveCouponsForUser() throws Exception {
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("KUBA20-TEST01");
        coupon.setUserId(203L);
        coupon.setIsUsed(false);

        when(couponService.findUnusedCoupons(203L)).thenReturn(List.of(coupon));

        mockMvc.perform(get("/api/orders/coupons/user/203"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code").value("KUBA20-TEST01"))
                .andExpect(jsonPath("$[0].userId").value(203))
                .andExpect(jsonPath("$[0].isUsed").value(false));
    }
}
