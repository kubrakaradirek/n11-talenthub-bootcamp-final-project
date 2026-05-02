package com.n11bootcamp.payment_service.dto;

public class CouponPreviewResponse {

    private boolean valid; // Kupon geçerli mi bilgisini order-service'den alıyoruz.
    private Double discountAmount; // İndirim tutarını ödeme servisinde göstermek için tutuyoruz.
    private Double discountedTotal; // Iyzico'ya gönderilecek indirimli toplamı tutuyoruz.
    private String message; // Kullanıcıya gösterilebilecek kısa mesajı tutuyoruz.

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }

    public Double getDiscountedTotal() { return discountedTotal; }
    public void setDiscountedTotal(Double discountedTotal) { this.discountedTotal = discountedTotal; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
