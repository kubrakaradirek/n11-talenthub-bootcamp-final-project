package com.n11bootcamp.payment_service.client;

import com.n11bootcamp.payment_service.dto.OrderRequest;
import com.n11bootcamp.payment_service.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service")
public interface OrderClient {

    @PostMapping("/api/orders")
    OrderResponse createOrder(@RequestBody OrderRequest request);
}
