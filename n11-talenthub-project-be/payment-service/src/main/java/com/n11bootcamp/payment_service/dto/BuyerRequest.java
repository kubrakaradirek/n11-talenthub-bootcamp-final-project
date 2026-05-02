package com.n11bootcamp.payment_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class BuyerRequest {

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ayse02")
    private String id;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ayse")
    private String name;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Karagul")
    private String surname;

    @Email
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ayse@gmail.com")
    private String email;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "11111111111")
    private String identityNumber;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "5555555555")
    private String gsmNumber;

    private String registrationAddress;
    private String city;
    private String country;
    private String ip;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getGsmNumber() {
        return gsmNumber;
    }

    public void setGsmNumber(String gsmNumber) {
        this.gsmNumber = gsmNumber;
    }

    public String getRegistrationAddress() {
        return registrationAddress;
    }

    public void setRegistrationAddress(String registrationAddress) {
        this.registrationAddress = registrationAddress;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
