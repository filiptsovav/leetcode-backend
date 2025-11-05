package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.AppUser;
import com.example.demo.repository.UserRepository;


@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/{username}")
    public String getAllRecordsByUser(Model model, @PathVariable String username) {
        AppUser user = userRepository.findByUsername(username);
        if (user == null) {
            return "404"; //TODO
        }
        model.addAttribute("user", user);
        return "user";
    }

    @PostMapping("/add")
    public String makeNewRecord(@RequestParam String username, @RequestParam String content) {
        AppUser newUser = new AppUser(username, content);
        userRepository.save(newUser);
        return "redirect:/dashboard";
    }
}
