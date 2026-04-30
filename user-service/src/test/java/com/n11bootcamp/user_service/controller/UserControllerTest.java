package com.n11bootcamp.user_service.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11bootcamp.user_service.request.SignupRequest;
import com.n11bootcamp.user_service.response.MessageResponse;
import com.n11bootcamp.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Spring Security varsa testte engellememesi için
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnOkWhenSignupIsValid() throws Exception {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setUsername("kubra_dev");
        request.setEmail("kubra@n11.com");
        request.setPassword("12345678");

        when(userService.registerUser(any(SignupRequest.class)))
                .thenReturn((ResponseEntity) ResponseEntity.ok(new MessageResponse("Başarılı!")));

        // Act & Assert
        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenSignupDataIsInvalid() throws Exception {
        // Geçersiz veri hazırlıyoruz (Validation kurallarını kırmak için)
        SignupRequest request = new SignupRequest();
        request.setUsername("kb"); // Kural: min 3 karakter olmalı
        request.setEmail("yanlis-email-formati"); // Kural: @Email olmalı
        request.setPassword("123"); // Kural: min 6 karakter olmalı

        // UserService hiç çağrılmadan Controller'ın 400 Bad Request fırlatması için
        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}