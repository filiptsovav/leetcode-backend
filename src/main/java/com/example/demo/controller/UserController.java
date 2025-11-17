package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/{username}")
    public String getAllRecordsByUser(Model model, @PathVariable String username) {
        log.info("GET /users/{}", username);

        AppUser user = userRepository.findByUsername(username);

        if (user == null) {
            log.warn("Пользователь {} не найден", username);
            return "404";
        }

        model.addAttribute("user", user);

        log.info("Пользователь {} найден. Отображаем страницу.", username);
        return "user";
    }

    @PostMapping("/add")
    public String makeNewRecord(@RequestParam String username, @RequestParam String content) {
        log.info("POST /add — создание пользователя {}", username);

        AppUser newUser = new AppUser(username, content);
        userRepository.save(newUser);

        log.info("Пользователь {} успешно создан", username);

        return "redirect:/dashboard";
    }
}
