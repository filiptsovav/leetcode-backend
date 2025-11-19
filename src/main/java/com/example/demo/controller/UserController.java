package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "User Pages", description = "HTML страницы для отображения данных пользователя")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Operation(
            summary = "Просмотр профиля пользователя",
            description = "Возвращает HTML-страницу с данными пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница успешно загружена"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @GetMapping("/users/{username}")
    public String getAllRecordsByUser(
            Model model,
            @PathVariable @Schema(example = "john_doe") String username) {

        log.info("GET /users/{}", username);
        AppUser user = userRepository.findByUsername(username);

        if (user == null) {
            log.warn("Пользователь {} не найден", username);
            return "404";
        }

        model.addAttribute("user", user);
        return "user";
    }


    @Operation(
            summary = "Создать нового пользователя",
            description = "Добавляет нового пользователя в систему и перенаправляет на дашборд",
            responses = {
                    @ApiResponse(responseCode = "302", description = "Успешно создано, перенаправлено"),
            }
    )
    @PostMapping("/add")
    public String makeNewRecord(
            @RequestParam @Schema(example = "john_doe") String username,
            @RequestParam @Schema(example = "some text") String content) {

        log.info("POST /add — создание пользователя {}", username);

        AppUser newUser = new AppUser(username, content);
        userRepository.save(newUser);

        return "redirect:/dashboard";
    }
}
