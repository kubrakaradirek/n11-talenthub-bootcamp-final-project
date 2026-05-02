package com.n11bootcamp.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateOrderRequest {

    @NotBlank(message = "Kullanıcı adı boş olamaz") // Bu alan boş gelirse validasyon hatası verir.
    private String username;
    @NotBlank(message = "Ad boş olamaz") // Müşteri adı zorunlu olsun diye kullanıyoruz.
    private String firstName;
    @NotBlank(message = "Soyad boş olamaz") // Müşteri soyadı zorunlu olsun diye kullanıyoruz.
    private String lastName;
    @NotBlank(message = "Adres boş olamaz") // Teslimat adresi boş kalmasın diye kullanıyoruz.
    private String streetAddress;
    @NotBlank(message = "Şehir boş olamaz") // Şehir bilgisi zorunlu olsun diye kullanıyoruz.
    private String city;
    @NotBlank(message = "Ülke boş olamaz") // Ülke bilgisi zorunlu olsun diye kullanıyoruz.
    private String country;
    @NotBlank(message = "Telefon boş olamaz") // Telefon bilgisi zorunlu olsun diye kullanıyoruz.
    private String phone;
    @Email(message = "Email formatı hatalı") // Email yanlış yazılırsa validasyon hatası verir.
    @NotBlank(message = "Email boş olamaz") // Email alanının boş kalmasını engelliyoruz.
    private String email;
    @Valid // Listenin içindeki ürünleri de kontrol etmek için kullanıyoruz.
    @NotEmpty(message = "Sipariş en az bir ürün içermelidir") // Sepet boşsa sipariş oluşmasın diye kullanıyoruz.
    private List<OrderItemRequest> items;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public static class OrderItemRequest {
        @NotNull(message = "Ürün id boş olamaz") // Hangi ürün alınacak bilmek için zorunlu.
        private Long productId;
        @NotBlank(message = "Ürün adı boş olamaz") // Ürün adını sipariş detayında göstermek için zorunlu.
        private String productName;
        @NotNull(message = "Fiyat boş olamaz") // Toplam tutarı hesaplamak için zorunlu.
        @DecimalMin(value = "1.0", message = "Fiyat 1 veya daha büyük olmalıdır") // Sıfır veya negatif fiyatı engelliyoruz.
        private Double price;
        @NotNull(message = "Adet boş olamaz") // Kaç adet alınacağını bilmek için zorunlu.
        @Min(value = 1, message = "Adet 1 veya daha büyük olmalıdır") // Sıfır veya negatif adedi engelliyoruz.
        private Integer quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
