package com.n11bootcamp.order_service.exception; // Bu sınıfın hata yönetimi paketinde olduğunu söylüyoruz.

import org.springframework.http.ResponseEntity; // HTTP cevabını status koduyla dönebilmek için kullanıyoruz.
import org.springframework.web.bind.MethodArgumentNotValidException; // @Valid hatalarını yakalamak için kullanıyoruz.
import org.springframework.web.bind.annotation.ExceptionHandler; // Hangi hata türünü yakalayacağımızı belirtmek için kullanıyoruz.
import org.springframework.web.bind.annotation.RestControllerAdvice; // Tüm controller hatalarını tek yerden yakalamak için kullanıyoruz.

@RestControllerAdvice // Controller içinde oluşan hataları bu sınıfa yönlendiriyoruz.
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class) // RuntimeException türündeki iş hatalarını yakalıyoruz.
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException exception) { // İş hatası gelince bu metot çalışır.
        ErrorResponse errorResponse = new ErrorResponse(400, exception.getMessage()); // Basit hata JSON cevabını hazırlıyoruz.
        return ResponseEntity.badRequest().body(errorResponse); // Kullanıcıya 400 koduyla hata cevabı dönüyoruz.
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // Kullanıcı eksik veya hatalı bilgi girerse bu hata yakalanır.
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) { // Validasyon hatası gelince bu metot çalışır.
        String message = exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage(); // İlk validasyon mesajını alıyoruz.
        ErrorResponse errorResponse = new ErrorResponse(400, message); // Mesajı bizim basit JSON formatımıza koyuyoruz.
        return ResponseEntity.badRequest().body(errorResponse); // Kullanıcıya 400 koduyla validasyon cevabı dönüyoruz.
    }
}
