package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Registration request object")
public class RegisterRequest {
    @Schema(description = "New username", example = "newuser")
    private String username;
    @Schema(description = "Password", example = "strongPassword123")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
