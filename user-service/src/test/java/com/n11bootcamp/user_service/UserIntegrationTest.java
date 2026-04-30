package com.n11bootcamp.user_service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11bootcamp.user_service.repository.UserRepository;
import com.n11bootcamp.user_service.request.LoginRequest;
import com.n11bootcamp.user_service.request.SignupRequest;
import com.n11bootcamp.user_service.service.KeycloakService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KeycloakService keycloakService;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSignUpNewUser() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("yeni_user");
        request.setEmail("yeni@n11.com");
        request.setPassword("sifre123");

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenSignupDataIsInvalid() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("kb"); // 3 karakterden kısa (Validation testi)
        request.setEmail("gecersiz-email");

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}