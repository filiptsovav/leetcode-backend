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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Task Records", description = "API для записи выполненных задач")
public class TaskController {

    @Autowired
    private LeetCodeApiService leetCodeApiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Operation(
            summary = "Сохранить выполненную задачу",
            description = "Записывает факт решения задачи пользователем",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запись успешно сохранена",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"success\": true }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректное имя задачи",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Task does not exist on LeetCode\" }")
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Неавторизован"),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
            }
    )
    @PostMapping("/taskChosen")
    @Transactional
    public ResponseEntity<?> submitTask(
            @RequestParam @Schema(example = "two-sum") String taskName,
            @RequestParam @Schema(example = "15", description = "Время решения в минутах") String taskTime,
            @RequestParam @Schema(example = "2025-01-26") String completionDate,
            @RequestParam @Schema(example = "1") String attemptNumber) {


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