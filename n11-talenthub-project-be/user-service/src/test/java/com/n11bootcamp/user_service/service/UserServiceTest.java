package com.n11bootcamp.user_service.service;


import com.n11bootcamp.user_service.entity.ShoppingCart;
import com.n11bootcamp.user_service.entity.User;
import com.n11bootcamp.user_service.repository.UserRepository;
import com.n11bootcamp.user_service.request.SignupRequest;
import com.n11bootcamp.user_service.request.UpdateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully() {
        SignupRequest request = new SignupRequest();
        request.setUsername("kubra");
        request.setEmail("kubra@n11.com");
        request.setPassword("123456");

        when(userRepository.existsByUsername("kubra")).thenReturn(false);
        when(userRepository.existsByEmail("kubra@n11.com")).thenReturn(false);

        ResponseEntity<?> response = userService.registerUser(request);

        assertEquals(200, response.getStatusCode().value());
        verify(keycloakService).createUserInKeycloak(any());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldFailRegisterWhenEmailAlreadyExists() {
        SignupRequest request = new SignupRequest();
        request.setUsername("kubra");
        request.setEmail("duplicate@n11.com");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("duplicate@n11.com")).thenReturn(true);

        ResponseEntity<?> response = userService.registerUser(request);

        assertEquals(400, response.getStatusCode().value());
        com.n11bootcamp.user_service.response.MessageResponse body =
                (com.n11bootcamp.user_service.response.MessageResponse) response.getBody();

        assertNotNull(body);
        assertEquals("Hata: Bu email adresi zaten kullanımda!", body.getMessage());
        verify(keycloakService, never()).createUserInKeycloak(any());
    }

    @Test
    void shouldUpdateUserEmailSuccessfully() {
        Long userId = 1L;
        User existingUser = new User("kubra", "old@n11.com", "pass");
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setEmail("new@n11.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@n11.com")).thenReturn(false);

        ResponseEntity<?> response = userService.updateUser(userId, updateRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("new@n11.com", existingUser.getEmail());
    }

    @Test
    void shouldDeleteUserAndAttemptToDeleteShoppingCart() {
        Long userId = 1L;
        User user = new User("kubra", "kubra@n11.com", "pass");
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Sepeti bulduğunu simüle et
        ShoppingCart mockCart = new ShoppingCart();
        mockCart.setId(100L);
        when(restTemplate.getForObject(anyString(), eq(ShoppingCart.class))).thenReturn(mockCart);

        ResponseEntity<?> response = userService.deleteUser(userId);

        assertEquals(200, response.getStatusCode().value());
        verify(restTemplate).delete("http://SHOPPING-CART-SERVICE/api/shopping-cart/100");
        verify(userRepository).delete(user);
    }
}