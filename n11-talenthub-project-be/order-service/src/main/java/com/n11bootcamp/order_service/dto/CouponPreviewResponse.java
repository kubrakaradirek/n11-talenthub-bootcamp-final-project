package com.n11bootcamp.order_service.dto;

public class CouponPreviewResponse {

    private boolean valid; // Kupon geçerli mi bilgisini frontend ve payment-service için tutuyoruz.
    private Double discountAmount; // İndirim tutarını gösteriyoruz.
    private Double discountedTotal; // İndirim sonrası ödenecek tutarı gösteriyoruz.
    private String message; // Kullanıcıya gösterilecek kısa mesajı tutuyoruz.

    public CouponPreviewResponse(boolean valid, Double discountAmount, Double discountedTotal, String message) {
        this.valid = valid;
        this.discountAmount = discountAmount;
        this.discountedTotal = discountedTotal;
        this.message = message;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }

    public Double getDiscountedTotal() { return discountedTotal; }
    public void setDiscountedTotal(Double discountedTotal) { this.discountedTotal = discountedTotal; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
