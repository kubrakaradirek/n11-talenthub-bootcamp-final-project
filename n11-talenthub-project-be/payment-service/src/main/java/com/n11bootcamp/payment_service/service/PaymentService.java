package com.n11bootcamp.payment_service.service;

import com.n11bootcamp.payment_service.dto.PaymentRequest;
import com.n11bootcamp.payment_service.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse pay(PaymentRequest request);
}
