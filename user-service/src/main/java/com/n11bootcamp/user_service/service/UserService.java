package com.n11bootcamp.user_service.service;


import java.util.ArrayList;
import java.util.List;

import com.n11bootcamp.user_service.entity.ShoppingCart;
import com.n11bootcamp.user_service.entity.User;
import com.n11bootcamp.user_service.repository.UserRepository;
import com.n11bootcamp.user_service.request.LoginRequest;
import com.n11bootcamp.user_service.request.SignupRequest;
import com.n11bootcamp.user_service.request.UpdateUserRequest;
import com.n11bootcamp.user_service.response.JwtResponse;
import com.n11bootcamp.user_service.response.MessageResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    @Value("${jwt.issuer_uri}")
    private String jwtIssuerUri;

    @Value("${jwt.client_id}")
    private String jwtClientId;

    @Value("${jwt.client_secret}")
    private String jwtClientSecret;

    @Value("${jwt.grant_type}")
    private String jwtGrantType;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KeycloakService keycloakService;

    // Şifreleyiciyi sınıf seviyesinde bir kez tanımlamak daha performanslıdır
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 1. YENİ KULLANICI KAYDI (SIGNUP)
     * Önce kendi DB'mizi kontrol eder, sonra Keycloak'a yazar, en son kendi DB'mize kaydeder.
     */
    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        // A. Veritabanı Kontrolleri
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Hata: Bu kullanıcı adı zaten alınmış!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Hata: Bu email adresi zaten kullanımda!"));
        }

        // B. Keycloak'a Kaydet (Eğer Keycloak hata verirse kod burada kesilir, DB'ye hatalı kayıt atılmaz)
        keycloakService.createUserInKeycloak(signUpRequest);

        // C. Kendi Veritabanımıza Kaydet
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()), // Şifreyi şifreleyerek kaydetmek güvenlik için şarttır
                "Customer"
        );
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Kullanıcı hem Keycloak'a hem de sisteme başarıyla kaydedildi!"));
    }

    /**
     * 2. KULLANICI GİRİŞİ (SIGNIN)
     * Kullanıcıyı DB'den bulur, Keycloak'tan Token alır.
     */
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        User user;
        try {
            // Kullanıcıyı veritabanından bul
            user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Hata: Kullanıcı bulunamadı!"));
        }

        // Apache HttpClient ile Keycloak'tan Token İsteği
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(jwtIssuerUri.trim());

        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", jwtGrantType.trim()));
        params.add(new BasicNameValuePair("client_id", jwtClientId.trim()));
        params.add(new BasicNameValuePair("client_secret", jwtClientSecret.trim()));
        params.add(new BasicNameValuePair("username", loginRequest.getUsername().trim()));
        params.add(new BasicNameValuePair("password", loginRequest.getPassword().trim()));

        String accessToken = "";
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            accessToken = extractAccessToken(responseBody);

            // Şifre yanlışsa Keycloak token vermez, bunu yakalayalım
            if (accessToken == null || accessToken.isEmpty()) {
                return ResponseEntity.status(401).body(new MessageResponse("Hata: Kimlik doğrulaması başarısız! Şifrenizi kontrol edin."));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new MessageResponse("Hata: Keycloak sunucusuna ulaşılamadı."));
        }

        // Token ve kullanıcı detaylarını dön
        return ResponseEntity.ok(new JwtResponse(accessToken, user.getId(), user.getUsername(), user.getEmail(), user.getRole()));
    }

    /**
     * YARDIMCI METOD: JSON response'tan "access_token" değerini çıkarır.
     */
    private static String extractAccessToken(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            return rootNode.path("access_token").asText();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 3. KULLANICIYI SİLME
     */
    public ResponseEntity<?> deleteUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found!"));

            // Kullanıcının sepeti varsa sil
            try {
                ShoppingCart shoppingCart = restTemplate.getForObject(
                        "http://SHOPPING-CART-SERVICE/api/shopping-cart/by-name/" + user.getUsername(),
                        ShoppingCart.class);

                if(shoppingCart != null && shoppingCart.getId() != null) {
                    restTemplate.delete("http://SHOPPING-CART-SERVICE/api/shopping-cart/" + shoppingCart.getId());
                }
            } catch (Exception e) {
                // Sepet bulunamazsa işlemi kesme, devam et
            }

            // DB'den kullanıcıyı sil
            userRepository.delete(user);

            // İleri Seviye Not: Gelecekte buraya KeycloakService içinden
            // kullanıcıyı Keycloak'tan da silecek bir metod eklenebilir.

            return ResponseEntity.ok(new MessageResponse("Kullanıcı başarıyla silindi!"));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new MessageResponse("Internal Server Error"));
        }
    }

    /**
     * 4. KULLANICI GÜNCELLEME
     */
    public ResponseEntity<?> updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found!"));

            // Şifre güncelleniyorsa tekrar şifrele
            if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
            }

            // Email güncelleniyorsa başkasında var mı diye kontrol et
            if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isEmpty()) {
                // Sadece yeni email eskisinden farklıysa kontrol et
                if (!user.getEmail().equals(updateUserRequest.getEmail()) && userRepository.existsByEmail(updateUserRequest.getEmail())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Hata: Bu email adresi zaten kullanımda!"));
                }
                user.setEmail(updateUserRequest.getEmail());
            }

            userRepository.save(user);

            return ResponseEntity.ok(new MessageResponse("Kullanıcı bilgileri başarıyla güncellendi!"));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new MessageResponse("Internal Server Error"));
        }
    }
}