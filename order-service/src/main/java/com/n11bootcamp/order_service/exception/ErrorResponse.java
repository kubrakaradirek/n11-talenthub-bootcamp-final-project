package com.n11bootcamp.order_service.exception;

public record ErrorResponse(
        int hataKodu,
        String mesaj
) {
}
