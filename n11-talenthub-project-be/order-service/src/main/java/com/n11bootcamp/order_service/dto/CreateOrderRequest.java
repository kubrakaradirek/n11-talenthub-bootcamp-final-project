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

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    private String username;
    @NotNull(message = "Kullanıcı id boş olamaz")
    private Long userId;
    @NotBlank(message = "Ad boş olamaz")
    private String firstName;
    @NotBlank(message = "Soyad boş olamaz")
    private String lastName;
    @NotBlank(message = "Adres boş olamaz")
    private String streetAddress;
    @NotBlank(message = "Şehir boş olamaz")
    private String city;
    @NotBlank(message = "Ülke boş olamaz")
    private String country;
    @NotBlank(message = "Telefon boş olamaz")
    private String phone;
    @Email(message = "Email formatı hatalı")
    @NotBlank(message = "Email boş olamaz")
    private String email;
    private String couponCode;
    @Valid
    @NotEmpty(message = "Sipariş en az bir ürün içermelidir")
    private List<OrderItemRequest> items;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

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

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public static class OrderItemRequest {
        @NotNull(message = "Ürün id boş olamaz")
        private Long productId;
        @NotBlank(message = "Ürün adı boş olamaz")
        private String productName;
        @NotNull(message = "Fiyat boş olamaz")
        @DecimalMin(value = "1.0", message = "Fiyat 1 veya daha büyük olmalıdır")
        private Double price;
        @NotNull(message = "Adet boş olamaz")
        @Min(value = 1, message = "Adet 1 veya daha büyük olmalıdır")
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
