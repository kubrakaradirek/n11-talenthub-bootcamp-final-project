package com.n11bootcamp.product_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, exception.getMessage()));
    }

    @ExceptionHandler(InvalidProductPageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProductPageException(InvalidProductPageException exception) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(400, exception.getMessage()));
    }
}
