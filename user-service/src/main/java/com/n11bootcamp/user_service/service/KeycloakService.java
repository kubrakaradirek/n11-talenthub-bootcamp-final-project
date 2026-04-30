package com.n11bootcamp.user_service.service;

import com.n11bootcamp.user_service.request.SignupRequest;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KeycloakService {

    @Value("${jwt.client_id}")
    private String clientId;

    @Value("${jwt.client_secret}")
    private String clientSecret;

    // Keycloak'a bağlanmak için anahtar
    private Keycloak getKeycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8081")
                .realm("microservice-realm")
                .grantType("client_credentials")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    public void createUserInKeycloak(SignupRequest request) {
        Keycloak keycloak = getKeycloak();

        // Kullanıcı şablonunu oluşturma
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(true);

        // Kullanıcıyı Keycloak'a kaydet
        Response response = keycloak.realm("microservice-realm").users().create(user);

        if (response.getStatus() == 201) {
            // Başarıyla oluştuysa ID'sini al
            String userId = CreatedResponseUtil.getCreatedId(response);

            // Şifresini kalıcı (Temporary=false) olarak ayarla
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(request.getPassword());

            keycloak.realm("microservice-realm").users().get(userId).resetPassword(passwordCred);

            // "Customer" rolünü bul ve kullanıcıya ata
            RoleRepresentation customerRole = keycloak.realm("microservice-realm").roles().get("Customer").toRepresentation();
            keycloak.realm("microservice-realm").users().get(userId).roles().realmLevel().add(Collections.singletonList(customerRole));

        } else {
            // Eğer aynı isimde biri varsa veya başka hata olursa işlemi durdur
            throw new RuntimeException("Keycloak'ta kullanıcı oluşturulamadı! Hata Kodu: " + response.getStatus());
        }
    }
}