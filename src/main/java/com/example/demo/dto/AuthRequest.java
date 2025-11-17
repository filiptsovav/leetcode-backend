package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request object")
public class AuthRequest {
    @Schema(description = "Username", example = "john")
    private String username;
    @Schema(description = "Password", example = "secret123")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
