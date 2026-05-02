package com.n11bootcamp.product_service.exception;

public class ErrorResponse {

    private int hataKodu;
    private String mesaj;

    public ErrorResponse(int hataKodu, String mesaj) {
        this.hataKodu = hataKodu;
        this.mesaj = mesaj;
    }

    public int getHataKodu() {
        return hataKodu;
    }

    public void setHataKodu(int hataKodu) {
        this.hataKodu = hataKodu;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }
}
