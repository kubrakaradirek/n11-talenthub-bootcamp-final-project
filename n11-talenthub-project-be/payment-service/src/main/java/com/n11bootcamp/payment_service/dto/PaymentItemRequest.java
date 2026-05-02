package com.n11bootcamp.payment_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class PaymentItemRequest {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long productId;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "iPhone 15 Pro")
    private String productName;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "75000")
    private BigDecimal price;

    @NotNull
    @Min(1)
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer quantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
