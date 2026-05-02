package com.n11bootcamp.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class CardRequest {

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ayse Karagul")
    private String cardHolderName;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "5528790000000008")
    private String cardNumber;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "12")
    private String expireMonth;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2030")
    private String expireYear;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private String cvc;

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpireMonth() {
        return expireMonth;
    }

    public void setExpireMonth(String expireMonth) {
        this.expireMonth = expireMonth;
    }

    public String getExpireYear() {
        return expireYear;
    }

    public void setExpireYear(String expireYear) {
        this.expireYear = expireYear;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
}
