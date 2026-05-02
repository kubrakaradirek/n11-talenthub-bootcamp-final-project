package com.n11bootcamp.order_service.dto;

public class StockUpdateResponse {

    private boolean success;
    private String message;

    public StockUpdateResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
