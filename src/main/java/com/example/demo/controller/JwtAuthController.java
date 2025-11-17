package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.AppUser;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "JWT Authentication", description = "Эндпоинты для регистрации и получения JWT-токена")
public class JwtAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Operation(
            summary = "Авторизация пользователя",
            description = "Проверяет логин и пароль, возвращает JWT-токен при успешной аутентификации.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешная авторизация",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Неверный логин или пароль")
            }
    )
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtUtil.generateToken(request.getUsername());

        return new AuthResponse(token);
    }


    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создаёт нового пользователя в системе.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
                    @ApiResponse(responseCode = "400", description = "Пользователь уже существует")
            }
    )
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()) != null) {
            return "User already exists";
        }

        AppUser user = new AppUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);

        return "Registered successfully";
    }
}
