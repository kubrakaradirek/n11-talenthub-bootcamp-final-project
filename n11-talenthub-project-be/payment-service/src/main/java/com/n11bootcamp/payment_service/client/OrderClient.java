package com.n11bootcamp.payment_service.client;

import com.n11bootcamp.payment_service.dto.OrderRequest;
import com.n11bootcamp.payment_service.dto.OrderResponse;
import com.n11bootcamp.payment_service.dto.CouponPreviewResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service")
public interface OrderClient {

    @PostMapping("/api/orders")
    OrderResponse createOrder(@RequestBody OrderRequest request);

    @GetMapping("/api/orders/coupons/preview")
    CouponPreviewResponse previewCoupon(
            @RequestParam Long userId,
            @RequestParam String couponCode,
            @RequestParam Double totalPrice);
}
