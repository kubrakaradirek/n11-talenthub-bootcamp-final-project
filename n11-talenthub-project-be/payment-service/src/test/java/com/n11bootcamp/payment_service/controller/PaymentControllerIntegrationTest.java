package com.n11bootcamp.payment_service.controller;

import com.n11bootcamp.payment_service.dto.PaymentResponse;
import com.n11bootcamp.payment_service.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PaymentController.class, PaymentExceptionHandler.class})
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void payReturnsCreatedResponse() throws Exception {
        PaymentResponse response = new PaymentResponse();
        response.setSuccessful(true);
        response.setOrderId(42L);
        response.setStatus("success");
        response.setPaymentId("pay-1");

        when(paymentService.pay(any())).thenReturn(response);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(jsonPath("$.orderId").value(42))
                .andExpect(jsonPath("$.paymentId").value("pay-1"));
    }

    @Test
    void payReturnsBadRequestWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.successful").value(false))
                .andExpect(jsonPath("$.status").value("validation_error"));
    }

    private String validJson() {
        return """
                {
                  "username": "demo",
                  "buyer": {
                    "id": "1",
                    "name": "Ada",
                    "surname": "Lovelace",
                    "email": "ada@example.com",
                    "identityNumber": "11111111111",
                    "gsmNumber": "+905350000000",
                    "registrationAddress": "Test Street",
                    "city": "Istanbul",
                    "country": "Turkey",
                    "ip": "127.0.0.1"
                  },
                  "card": {
                    "cardHolderName": "Ada Lovelace",
                    "cardNumber": "5528790000000008",
                    "expireMonth": "12",
                    "expireYear": "2030",
                    "cvc": "123"
                  },
                  "billingAddress": {
                    "address": "Test Street",
                    "city": "Istanbul",
                    "country": "Turkey",
                    "zipCode": "34000"
                  },
                  "shippingAddress": {
                    "address": "Test Street",
                    "city": "Istanbul",
                    "country": "Turkey",
                    "zipCode": "34000"
                  },
                  "items": [
                    {
                      "productId": 1,
                      "productName": "iPhone 15 Pro",
                      "price": 75000.00,
                      "quantity": 1
                    }
                  ]
                }
                """;
    }
}
