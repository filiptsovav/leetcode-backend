package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.model.TaskRecord;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LeetCodeApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.time.format.DateTimeFormatter;
import com.example.demo.repository.RecordRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.leetCodeApiService.Question;


import java.time.DayOfWeek;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
public class TaskController {

    @Autowired
    private LeetCodeApiService leetCodeApiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

    @PostMapping("/taskChosen")
    @Transactional
    public ResponseEntity<?> submitTask(
            @RequestParam String taskName,
            @RequestParam String taskTime,
            @RequestParam String completionDate,
            @RequestParam String attemptNumber) {

        try {
            leetCodeApiService.getQuestion(taskName);
        } catch (Exception e) {
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

        return ResponseEntity.ok(Map.of("success", true));
    }
}
