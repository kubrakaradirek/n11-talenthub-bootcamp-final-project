package com.n11bootcamp.user_service.controller;

import com.n11bootcamp.user_service.request.LoginRequest;
import com.n11bootcamp.user_service.request.SignupRequest;
import com.n11bootcamp.user_service.request.UpdateUserRequest;
import com.n11bootcamp.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
@Tag(name = "Users", description = "User registration, login, and profile operations")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/signin")
    @Operation(summary = "Sign in", description = "Authenticates a user through Keycloak and returns JWT information.")
    public ResponseEntity<?> authenticateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        return userService.authenticateUser(loginRequest);
    }

    @PostMapping("/signup")
    @Operation(summary = "Sign up", description = "Creates the user in Keycloak and stores user data in user-service.")
    public ResponseEntity<?> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true)
            @Valid @RequestBody SignupRequest signUpRequest) {
        return userService.registerUser(signUpRequest);
    }

    @GetMapping("/test")
    @Hidden
    @Operation(summary = "Validate token", description = "Protected test endpoint for checking JWT access through the Gateway.")
    public ResponseEntity<String> testToken() {
        return ResponseEntity.ok("Token verified successfully.");
    }

    @DeleteMapping("/delete/{userId}")
    @Hidden
    @Operation(summary = "Delete user")
    public ResponseEntity<?> deleteUser(@Parameter(required = true) @PathVariable Long userId) {
        return userService.deleteUser(userId);
    }

    @PutMapping("/update/{userId}")
    @Hidden
    @Operation(summary = "Update user")
    public ResponseEntity<?> updateUser(
            @Parameter(required = true) @PathVariable Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true)
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(userId, updateUserRequest);
    }
}
