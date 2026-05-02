package com.n11bootcamp.payment_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class PaymentRequest {

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ayse02")
    private String username;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "203")
    private Long userId;

    @Valid
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private BuyerRequest buyer;

    @Valid
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private CardRequest card;

    @Valid
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private AddressRequest billingAddress;

    @Valid
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private AddressRequest shippingAddress;

    @Valid
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PaymentItemRequest> items;

    @Schema(example = "KUBA20-ABC123")
    private String couponCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BuyerRequest getBuyer() {
        return buyer;
    }

    public void setBuyer(BuyerRequest buyer) {
        this.buyer = buyer;
    }

    public CardRequest getCard() {
        return card;
    }

    public void setCard(CardRequest card) {
        this.card = card;
    }

    public AddressRequest getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(AddressRequest billingAddress) {
        this.billingAddress = billingAddress;
    }

    public AddressRequest getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressRequest shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<PaymentItemRequest> getItems() {
        return items;
    }

    public void setItems(List<PaymentItemRequest> items) {
        this.items = items;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
