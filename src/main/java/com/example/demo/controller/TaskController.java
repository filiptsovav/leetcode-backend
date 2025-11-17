package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.model.TaskRecord;
import com.example.demo.model.leetCodeApiService.Question;
import com.example.demo.repository.RecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LeetCodeApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import com.example.demo.repository.RecordRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.leetCodeApiService.Question;


import java.time.DayOfWeek;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Controller
public class LeetCodeApiServiceExampleController {

    private static final Logger log = LoggerFactory.getLogger(LeetCodeApiServiceExampleController.class);

@RestController
public class TaskController {

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
    public ResponseEntity<?> submitTask(
            @RequestParam String taskName,
            @RequestParam String taskTime,
            @RequestParam String completionDate,
            @RequestParam String attemptNumber) {

    }
        log.info("POST /taskChosen — отправка задачи {} ", taskName);

        try {
            leetCodeApiService.getQuestion(taskName);
        } catch (Exception e) {
        log.error("Ошибка: задача {} не найдена", taskName);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Task does not exist on LeetCode"));


        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AppUser appUser = userRepository.findByUsername(username);

        LocalDateTime date = LocalDate
                .parse(completionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atStartOfDay();

        Duration duration = Duration.ofMinutes(Long.parseLong(taskTime));
        Integer attempts = Integer.parseInt(attemptNumber);

        TaskRecord record = new TaskRecord(taskName, date, duration, attempts);
        recordRepository.save(record);

        appUser.addRecord(record);
        userRepository.save(appUser);
        log.info("Задача {} сохранена пользователю {}", taskName, currentUser.getUsername());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
