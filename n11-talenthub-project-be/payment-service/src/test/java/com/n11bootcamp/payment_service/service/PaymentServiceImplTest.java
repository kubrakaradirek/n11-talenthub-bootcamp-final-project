package com.n11bootcamp.payment_service.service;

import com.iyzipay.model.Payment;
import com.iyzipay.request.CreatePaymentRequest;
import com.n11bootcamp.payment_service.client.IyzipayHttpClient;
import com.n11bootcamp.payment_service.client.OrderClient;
import com.n11bootcamp.payment_service.dto.AddressRequest;
import com.n11bootcamp.payment_service.dto.BuyerRequest;
import com.n11bootcamp.payment_service.dto.CardRequest;
import com.n11bootcamp.payment_service.dto.OrderRequest;
import com.n11bootcamp.payment_service.dto.OrderResponse;
import com.n11bootcamp.payment_service.dto.PaymentItemRequest;
import com.n11bootcamp.payment_service.dto.PaymentRequest;
import com.n11bootcamp.payment_service.dto.PaymentResponse;
import com.n11bootcamp.payment_service.exception.PaymentFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceImplTest {

    private OrderClient orderClient;
    private IyzipayHttpClient iyzipayHttpClient;
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        orderClient = mock(OrderClient.class);
        iyzipayHttpClient = mock(IyzipayHttpClient.class);
        paymentService = new PaymentServiceImpl(orderClient, iyzipayHttpClient);
    }

    @Test
    void payCreatesOrderAndReturnsSuccessfulPayment() {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(42L);
        orderResponse.setUsername("demo");
        orderResponse.setStatus("CREATED");
        when(orderClient.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);

        Payment payment = new Payment();
        payment.setStatus("success");
        payment.setPaymentId("pay-1");
        payment.setConversationId("conversation-1");
        when(iyzipayHttpClient.createPayment(any(CreatePaymentRequest.class))).thenReturn(payment);

        PaymentResponse response = paymentService.pay(validRequest());

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getOrderId()).isEqualTo(42L);
        assertThat(response.getPaymentId()).isEqualTo("pay-1");
        assertThat(response.getPaidPrice()).isEqualByComparingTo("150000.00");

        ArgumentCaptor<OrderRequest> orderCaptor = ArgumentCaptor.forClass(OrderRequest.class);
        verify(orderClient).createOrder(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getItems()).hasSize(1);
    }

    @Test
    void payThrowsPaymentFailedExceptionWhenIyzipayDeclines() {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(42L);
        when(orderClient.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);

        Payment payment = new Payment();
        payment.setStatus("failure");
        payment.setErrorMessage("card declined");
        when(iyzipayHttpClient.createPayment(any(CreatePaymentRequest.class))).thenReturn(payment);

        assertThatThrownBy(() -> paymentService.pay(validRequest()))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessage("card declined");
    }

    private PaymentRequest validRequest() {
        PaymentRequest request = new PaymentRequest();
        request.setUsername("demo");
        request.setBuyer(buyer());
        request.setCard(card());
        request.setBillingAddress(address());
        request.setShippingAddress(address());
        request.setItems(List.of(item()));
        return request;
    }

    private BuyerRequest buyer() {
        BuyerRequest buyer = new BuyerRequest();
        buyer.setId("1");
        buyer.setName("Ada");
        buyer.setSurname("Lovelace");
        buyer.setEmail("ada@example.com");
        buyer.setGsmNumber("+905350000000");
        buyer.setIdentityNumber("11111111111");
        buyer.setRegistrationAddress("Test Street");
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        buyer.setIp("127.0.0.1");
        return buyer;
    }

    private CardRequest card() {
        CardRequest card = new CardRequest();
        card.setCardHolderName("Ada Lovelace");
        card.setCardNumber("5528790000000008");
        card.setExpireMonth("12");
        card.setExpireYear("2030");
        card.setCvc("123");
        return card;
    }

    private AddressRequest address() {
        AddressRequest address = new AddressRequest();
        address.setAddress("Test Street");
        address.setCity("Istanbul");
        address.setCountry("Turkey");
        address.setZipCode("34000");
        return address;
    }

    private PaymentItemRequest item() {
        PaymentItemRequest item = new PaymentItemRequest();
        item.setProductId(1L);
        item.setProductName("iPhone 15 Pro");
        item.setPrice(new BigDecimal("75000.00"));
        item.setQuantity(2);
        return item;
    }
}
