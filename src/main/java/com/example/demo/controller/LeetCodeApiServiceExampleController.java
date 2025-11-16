package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.model.TaskRecord;
import com.example.demo.model.leetCodeApiService.Question;
import com.example.demo.repository.RecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LeetCodeApiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class LeetCodeApiServiceExampleController {

    private static final Logger log = LoggerFactory.getLogger(LeetCodeApiServiceExampleController.class);

    @Autowired
    private LeetCodeApiService leetCodeApiService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecordRepository recordRepository;

    @GetMapping("/getQuestion/{questionName}")
    public Question sendRequest(@PathVariable String questionName) {
        log.info("Запрос вопроса {}", questionName);
        return leetCodeApiService.getQuestion(questionName);
    }

    @PostMapping("/taskChosen")
    @Transactional
    public String submitTask(@RequestParam String taskName,
                             @RequestParam String taskTime,
                             @RequestParam String completionDate,
                             @RequestParam String attemptNumber,
                             HttpServletRequest request, Model model) {

        log.info("POST /taskChosen — отправка задачи {} ", taskName);

        try {
            leetCodeApiService.getQuestion(taskName);
        } catch (Exception e) {
            log.error("Ошибка: задача {} не найдена", taskName);
            return "redirect:/taskChosen?error=true";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        AppUser appUser = userRepository.findByUsername(currentUser.getUsername());

        LocalDateTime date = LocalDate.parse(completionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        Duration duration = Duration.ofMinutes(Long.parseLong(taskTime));
        Integer tryCounter = Integer.parseInt(attemptNumber);

        TaskRecord record = new TaskRecord(taskName, date, duration, tryCounter);
        recordRepository.save(record);
        appUser.addRecord(record);
        userRepository.save(appUser);

        log.info("Задача {} сохранена пользователю {}", taskName, currentUser.getUsername());

        return "redirect:/taskChosen?success=true";
    }

    @GetMapping("/taskSuggestion")
    public String taskSuggestion(Model model) {
        log.info("GET /taskSuggestion");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        AppUser appUser = userRepository.findByUsername(currentUser.getUsername());

        List<Question> recommendedTasks = appUser.getRecommendedTasks(leetCodeApiService);
        model.addAttribute("recommendedTasks", recommendedTasks);

        return "taskSuggestion";
    }
}
