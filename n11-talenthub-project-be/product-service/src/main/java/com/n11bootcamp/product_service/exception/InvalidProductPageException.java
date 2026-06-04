package com.n11bootcamp.product_service.exception;

public class InvalidProductPageException extends RuntimeException {

    // Geçersiz page veya size parametresi için fırlatılır.
    public InvalidProductPageException(String message) {
        super(message);
    }
}
