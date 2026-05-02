package com.n11bootcamp.payment_service.controller;

import com.n11bootcamp.payment_service.dto.PaymentResponse;
import com.n11bootcamp.payment_service.exception.PaymentFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(PaymentFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public PaymentResponse handlePaymentFailure(PaymentFailedException exception) {
        PaymentResponse response = new PaymentResponse();
        response.setSuccessful(false);
        response.setStatus("failure");
        response.setErrorMessage(exception.getMessage());
        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public PaymentResponse handleValidationError(MethodArgumentNotValidException exception) {
        PaymentResponse response = new PaymentResponse();
        response.setSuccessful(false);
        response.setStatus("validation_error");
        response.setErrorMessage("Payment request is not valid");
        return response;
    }
}
