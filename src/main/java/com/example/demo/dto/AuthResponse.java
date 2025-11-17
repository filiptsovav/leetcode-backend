package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing JWT token")
public class AuthResponse {
    @Schema(description = "JWT Token")
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
