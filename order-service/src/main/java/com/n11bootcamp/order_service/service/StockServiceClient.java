package com.n11bootcamp.order_service.service;

import com.n11bootcamp.order_service.dto.StockUpdateRequest;
import com.n11bootcamp.order_service.dto.StockUpdateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "stock-service", path = "/api/stocks")
public interface StockServiceClient {

    @PostMapping("/commit")
    StockUpdateResponse commit(@RequestBody StockUpdateRequest request);
}
