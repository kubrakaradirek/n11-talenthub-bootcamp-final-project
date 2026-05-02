package com.n11bootcamp.order_service.exception; // Bu sınıfın hata paketi içinde olduğunu söylüyoruz.

public record ErrorResponse( // Kullanıcıya döneceğimiz basit JSON modelini oluşturuyoruz.
        int hataKodu, // JSON içinde hata kodunu göstermek için kullanıyoruz.
        String mesaj // JSON içinde kullanıcıya okunabilir hata mesajı vermek için kullanıyoruz.
) {
}
