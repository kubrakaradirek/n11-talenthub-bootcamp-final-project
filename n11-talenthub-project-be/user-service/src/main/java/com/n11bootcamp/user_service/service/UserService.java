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

    // Şifreleri DB'ye düz metin yazmıyoruz.
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        // Önce aynı kullanıcı var mı bakıyoruz.
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Hata: Bu kullanıcı adı zaten alınmış!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Hata: Bu email adresi zaten kullanımda!"));
        }

        // Keycloak kaydı başarılı olursa kendi DB'mize geçiyoruz.
        keycloakService.createUserInKeycloak(signUpRequest);

        // Uygulamanın kendi kullanıcı kaydı
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                "Customer"
        );
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Kullanıcı hem Keycloak'a hem de sisteme başarıyla kaydedildi!"));
    }

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        User user;
        try {
            // Önce bizde kayıtlı mı kontrol ediyoruz.
            user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Hata: Kullanıcı bulunamadı!"));
        }

        // Keycloak token isteği
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

            // Token yoksa giriş başarısızdır.
            if (accessToken == null || accessToken.isEmpty()) {
                return ResponseEntity.status(401).body(new MessageResponse("Hata: Kimlik doğrulaması başarısız! Şifrenizi kontrol edin."));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new MessageResponse("Hata: Keycloak sunucusuna ulaşılamadı."));
        }

        // Frontend'e token ve kullanıcı bilgisini dönüyoruz.
        return ResponseEntity.ok(new JwtResponse(accessToken, user.getId(), user.getUsername(), user.getEmail(), user.getRole()));
    }

    private static String extractAccessToken(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            return rootNode.path("access_token").asText();
        } catch (Exception e) {
            return null;
        }
    }

    public ResponseEntity<?> deleteUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found!"));

            // Kullanıcının sepeti varsa onu da temizliyoruz.
            try {
                ShoppingCart shoppingCart = restTemplate.getForObject(
                        "http://SHOPPING-CART-SERVICE/api/shopping-cart/by-name/" + user.getUsername(),
                        ShoppingCart.class);

                if(shoppingCart != null && shoppingCart.getId() != null) {
                    restTemplate.delete("http://SHOPPING-CART-SERVICE/api/shopping-cart/" + shoppingCart.getId());
                }
            } catch (Exception e) {
                // Sepet yoksa sorun değil, kullanıcı silme devam eder.
            }

            userRepository.delete(user);

            return ResponseEntity.ok(new MessageResponse("Kullanıcı başarıyla silindi!"));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new MessageResponse("Internal Server Error"));
        }
    }

    public ResponseEntity<?> updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found!"));

            // Yeni şifre geldiyse tekrar şifreliyoruz.
            if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
            }

            // Email değişiyorsa çakışma var mı bakıyoruz.
            if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isEmpty()) {
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
