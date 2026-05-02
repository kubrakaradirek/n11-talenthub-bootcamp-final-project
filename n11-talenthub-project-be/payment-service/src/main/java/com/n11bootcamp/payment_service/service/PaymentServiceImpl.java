package com.n11bootcamp.payment_service.service;

import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.Currency;
import com.iyzipay.model.Locale;
import com.iyzipay.model.Payment;
import com.iyzipay.model.PaymentCard;
import com.iyzipay.model.PaymentChannel;
import com.iyzipay.model.PaymentGroup;
import com.iyzipay.request.CreatePaymentRequest;
import com.n11bootcamp.payment_service.client.IyzipayHttpClient;
import com.n11bootcamp.payment_service.client.OrderClient;
import com.n11bootcamp.payment_service.dto.AddressRequest;
import com.n11bootcamp.payment_service.dto.CouponPreviewResponse;
import com.n11bootcamp.payment_service.dto.OrderRequest;
import com.n11bootcamp.payment_service.dto.OrderResponse;
import com.n11bootcamp.payment_service.dto.PaymentItemRequest;
import com.n11bootcamp.payment_service.dto.PaymentRequest;
import com.n11bootcamp.payment_service.dto.PaymentResponse;
import com.n11bootcamp.payment_service.exception.PaymentFailedException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private static final String SUCCESS_STATUS = "success";

    private final OrderClient orderClient;
    private final IyzipayHttpClient iyzipayHttpClient;

    public PaymentServiceImpl(OrderClient orderClient, IyzipayHttpClient iyzipayHttpClient) {
        this.orderClient = orderClient;
        this.iyzipayHttpClient = iyzipayHttpClient;
    }

    @Override
    public PaymentResponse pay(PaymentRequest request) {
        validateRequiredIyzipayFields(request);
        BigDecimal originalTotal = totalPrice(request.getItems());
        BigDecimal payableTotal = payableTotal(request, originalTotal);
        CreatePaymentRequest iyzipayRequest = toIyzipayRequest(request, originalTotal, payableTotal);

        try {
            Payment payment = iyzipayHttpClient.createPayment(iyzipayRequest);
            if (!SUCCESS_STATUS.equalsIgnoreCase(payment.getStatus())) {
                log.error("Iyzipay payment failed conversationId={}, errorCode={}, errorMessage={}",
                        payment.getConversationId(), payment.getErrorCode(), payment.getErrorMessage());
                throw new PaymentFailedException(payment.getErrorMessage());
            }

            OrderResponse order = orderClient.createOrder(toOrderRequest(request));

            PaymentResponse response = new PaymentResponse();
            response.setSuccessful(true);
            response.setOrderId(order.getOrderId());
            response.setStatus(payment.getStatus());
            response.setPaymentId(payment.getPaymentId());
            response.setConversationId(payment.getConversationId());
            response.setPaidPrice(payableTotal);
            return response;
        } catch (PaymentFailedException exception) {
            throw exception;
        } catch (FeignException exception) {
            log.error("Order service error during payment status={}, body={}", exception.status(), exception.contentUTF8(), exception);
            throw new PaymentFailedException(readableFeignMessage(exception));
        } catch (Exception exception) {
            log.error("Unexpected payment error conversationId={}", iyzipayRequest.getConversationId(), exception);
            throw new PaymentFailedException("Payment could not be completed", exception);
        }
    }

    private OrderRequest toOrderRequest(PaymentRequest request) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUsername(request.getUsername());
        orderRequest.setUserId(request.getUserId());
        orderRequest.setFirstName(request.getBuyer().getName());
        orderRequest.setLastName(request.getBuyer().getSurname());
        orderRequest.setStreetAddress(request.getShippingAddress().getAddress());
        orderRequest.setCity(request.getShippingAddress().getCity());
        orderRequest.setCountry(request.getShippingAddress().getCountry());
        orderRequest.setPhone(request.getBuyer().getGsmNumber());
        orderRequest.setEmail(request.getBuyer().getEmail());
        orderRequest.setCouponCode(request.getCouponCode());
        orderRequest.setItems(request.getItems().stream().map(this::toOrderItem).toList());
        return orderRequest;
    }

    private OrderRequest.OrderItemRequest toOrderItem(PaymentItemRequest item) {
        OrderRequest.OrderItemRequest orderItem = new OrderRequest.OrderItemRequest();
        orderItem.setProductId(item.getProductId());
        orderItem.setProductName(item.getProductName());
        orderItem.setPrice(item.getPrice().doubleValue());
        orderItem.setQuantity(item.getQuantity());
        return orderItem;
    }

    private CreatePaymentRequest toIyzipayRequest(PaymentRequest request, BigDecimal originalTotal, BigDecimal payableTotal) {
        String conversationId = "payment-" + request.getUsername() + "-" + UUID.randomUUID();
        BigDecimal discountFactor = payableTotal.divide(originalTotal, 6, RoundingMode.HALF_UP);

        CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
        paymentRequest.setLocale(Locale.TR.getValue());
        paymentRequest.setConversationId(conversationId);
        paymentRequest.setPrice(payableTotal);
        paymentRequest.setPaidPrice(payableTotal);
        paymentRequest.setCurrency(Currency.TRY.name());
        paymentRequest.setInstallment(1);
        paymentRequest.setBasketId("cart-" + request.getUsername() + "-" + UUID.randomUUID());
        paymentRequest.setPaymentChannel(PaymentChannel.WEB.name());
        paymentRequest.setPaymentGroup(PaymentGroup.PRODUCT.name());
        paymentRequest.setPaymentCard(toPaymentCard(request));
        paymentRequest.setBuyer(toBuyer(request));
        paymentRequest.setBillingAddress(toAddress(request.getBillingAddress(), buyerFullName(request)));
        paymentRequest.setShippingAddress(toAddress(request.getShippingAddress(), buyerFullName(request)));
        paymentRequest.setBasketItems(toBasketItems(request.getItems(), discountFactor, payableTotal));
        logIyzipayRequestSummary(paymentRequest, request);
        return paymentRequest;
    }

    private PaymentCard toPaymentCard(PaymentRequest request) {
        PaymentCard card = new PaymentCard();
        card.setCardHolderName(request.getCard().getCardHolderName());
        card.setCardNumber(request.getCard().getCardNumber());
        card.setExpireMonth(request.getCard().getExpireMonth());
        card.setExpireYear(request.getCard().getExpireYear());
        card.setCvc(request.getCard().getCvc());
        card.setRegisterCard(0);
        return card;
    }

    private Buyer toBuyer(PaymentRequest request) {
        Buyer buyer = new Buyer();
        buyer.setId(request.getBuyer().getId());
        buyer.setName(request.getBuyer().getName());
        buyer.setSurname(request.getBuyer().getSurname());
        buyer.setGsmNumber(request.getBuyer().getGsmNumber());
        buyer.setEmail(request.getBuyer().getEmail());
        buyer.setIdentityNumber(request.getBuyer().getIdentityNumber());
        buyer.setRegistrationAddress(defaultIfBlank(
                request.getBuyer().getRegistrationAddress(), request.getBillingAddress().getAddress()));
        buyer.setIp(defaultIfBlank(request.getBuyer().getIp(), "127.0.0.1"));
        buyer.setCity(defaultIfBlank(request.getBuyer().getCity(), request.getBillingAddress().getCity()));
        buyer.setCountry(defaultIfBlank(request.getBuyer().getCountry(), request.getBillingAddress().getCountry()));
        return buyer;
    }

    private Address toAddress(AddressRequest request, String contactName) {
        Address address = new Address();
        address.setContactName(contactName);
        address.setAddress(request.getAddress());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address.setZipCode(request.getZipCode());
        return address;
    }

    private List<BasketItem> toBasketItems(List<PaymentItemRequest> items, BigDecimal discountFactor, BigDecimal payableTotal) {
        List<BasketItem> basketItems = new ArrayList<>();
        BigDecimal calculatedTotal = BigDecimal.ZERO; // Kuruş farkını takip ediyoruz.

        for (int i = 0; i < items.size(); i++) {
            PaymentItemRequest item = items.get(i);
            BasketItem basketItem = new BasketItem();
            basketItem.setId(String.valueOf(item.getProductId()));
            basketItem.setName(item.getProductName());
            basketItem.setCategory1("KubaShop");
            basketItem.setItemType(BasketItemType.PHYSICAL.name());
            BigDecimal itemPrice = lineTotal(item).multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
            if (i == items.size() - 1) { // Son kalemde kuruş farkını kapatıyoruz.
                itemPrice = payableTotal.subtract(calculatedTotal).setScale(2, RoundingMode.HALF_UP);
            }
            basketItem.setPrice(itemPrice);
            calculatedTotal = calculatedTotal.add(itemPrice);
            basketItems.add(basketItem);
        }

        return basketItems;
    }

    private BigDecimal payableTotal(PaymentRequest request, BigDecimal originalTotal) {
        if (request.getCouponCode() == null || request.getCouponCode().isBlank()) {
            return originalTotal;
        }

        CouponPreviewResponse coupon = orderClient.previewCoupon(
                request.getUserId(),
                request.getCouponCode(),
                originalTotal.doubleValue()); // Kuponu order-service'e kontrol ettiriyoruz.
        return BigDecimal.valueOf(coupon.getDiscountedTotal()).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal totalPrice(List<PaymentItemRequest> items) {
        return items.stream()
                .map(this::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal lineTotal(PaymentItemRequest item) {
        return item.getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String buyerFullName(PaymentRequest request) {
        return request.getBuyer().getName() + " " + request.getBuyer().getSurname();
    }

    private void validateRequiredIyzipayFields(PaymentRequest request) {
        requireText(request.getBuyer().getName(), "buyer.name");
        requireText(request.getBuyer().getSurname(), "buyer.surname");
        requireText(request.getBuyer().getEmail(), "buyer.email");
        requireText(request.getBuyer().getGsmNumber(), "buyer.gsmNumber");
        requireText(request.getBuyer().getIdentityNumber(), "buyer.identityNumber");
        requireText(defaultIfBlank(request.getBuyer().getRegistrationAddress(), request.getBillingAddress().getAddress()),
                "buyer.registrationAddress");
        requireText(defaultIfBlank(request.getBuyer().getCity(), request.getBillingAddress().getCity()), "buyer.city");
        requireText(defaultIfBlank(request.getBuyer().getCountry(), request.getBillingAddress().getCountry()), "buyer.country");
        requireText(request.getBillingAddress().getAddress(), "billingAddress.address");
        requireText(request.getBillingAddress().getCity(), "billingAddress.city");
        requireText(request.getBillingAddress().getCountry(), "billingAddress.country");
        requireText(request.getShippingAddress().getAddress(), "shippingAddress.address");
        requireText(request.getShippingAddress().getCity(), "shippingAddress.city");
        requireText(request.getShippingAddress().getCountry(), "shippingAddress.country");
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new PaymentFailedException("Missing required Iyzico field: " + fieldName);
        }
    }

    private String readableFeignMessage(FeignException exception) {
        String body = exception.contentUTF8();
        if (body == null || body.isBlank()) {
            return "Sipariş servisi ödeme sırasında hata döndü.";
        }
        String mesaj = extractJsonValue(body, "mesaj");
        if (mesaj != null) {
            return mesaj;
        }
        String errorMessage = extractJsonValue(body, "errorMessage");
        if (errorMessage != null) {
            return errorMessage;
        }
        return body;
    }

    private String extractJsonValue(String json, String fieldName) {
        String marker = "\"" + fieldName + "\":\"";
        int start = json.indexOf(marker);
        if (start < 0) {
            return null;
        }
        int valueStart = start + marker.length();
        int valueEnd = json.indexOf("\"", valueStart);
        if (valueEnd < 0) {
            return null;
        }
        return json.substring(valueStart, valueEnd);
    }

    private void logIyzipayRequestSummary(CreatePaymentRequest paymentRequest, PaymentRequest request) {
        log.info("Iyzipay request prepared conversationId={}, basketId={}, price={}, buyerEmail={}, buyerCity={}, buyerCountry={}, gsmNumberPresent={}, billingCity={}, shippingCity={}, itemCount={}",
                paymentRequest.getConversationId(),
                paymentRequest.getBasketId(),
                paymentRequest.getPrice(),
                request.getBuyer().getEmail(),
                defaultIfBlank(request.getBuyer().getCity(), request.getBillingAddress().getCity()),
                defaultIfBlank(request.getBuyer().getCountry(), request.getBillingAddress().getCountry()),
                request.getBuyer().getGsmNumber() != null && !request.getBuyer().getGsmNumber().isBlank(),
                request.getBillingAddress().getCity(),
                request.getShippingAddress().getCity(),
                request.getItems().size());
    }
}
