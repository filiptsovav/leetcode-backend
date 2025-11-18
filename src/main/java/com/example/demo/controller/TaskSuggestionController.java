package com.example.demo.controller;

import com.example.demo.model.AppUser;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.leetCodeApiService.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Tag(name = "Task Suggestions", description = "API рекомендаций задач")
public class TaskSuggestionController {

    @Autowired
    private LeetCodeApiService leetCodeApiService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(TaskSuggestionController.class);

    @Operation(
            summary = "Получить рекомендованные задачи",
            description = "Возвращает список задач, подобранных под пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Рекомендованные задачи успешно получены"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка получения рекомендаций",
                            content = @Content(
                                    schema = @Schema(example = "{ \"error\": \"Failed to load recommended tasks\" }")
                            )
                    )
            }
    )
    @GetMapping("/taskSuggestion")
    public ResponseEntity<?> getTaskSuggestion() {
        log.info("GET /taskSuggestion");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AppUser appUser = userRepository.findByUsername(username);

        try {
            List<Question> recommendedTasks = appUser.getRecommendedTasks(leetCodeApiService);
            return ResponseEntity.ok(recommendedTasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to load recommended tasks"));
        }
    }
}
