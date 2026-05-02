package com.n11bootcamp.user_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Invalid Username: Empty username")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ayse02")
    private String username;

    @NotBlank(message = "Invalid Password: Empty password")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
