package com.n11bootcamp.product_service.exception;

public class InvalidProductPageException extends RuntimeException {

    public InvalidProductPageException(String message) {
        super(message);
    }
}
