package com.n11bootcamp.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class AddressRequest {

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Test Mahallesi 1. Sokak No:1")
    private String address;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Istanbul")
    private String city;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Turkey")
    private String country;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "34000")
    private String zipCode;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
