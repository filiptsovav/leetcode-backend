package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LeetCodeApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/statistics")
@Tag(
        name = "User Statistics",
        description = "API для получения статистики решения задач пользователем на LeetCode"
)
public class StatisticsRestController {

    private static final Logger log = LoggerFactory.getLogger(StatisticsRestController.class);

    @Autowired
    private LeetCodeApiService leetCodeApiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Operation(
            summary = "Получить статистику пользователя",
            description = """
                    Возвращает агрегированную статистику решения задач для текущего авторизованного пользователя.
                    Доступна фильтрация по временным промежуткам.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное получение статистики",
                            content = @Content(
                                    mediaType = "application/json"
                                    // schema = @Schema(implementation = StatisticsResponseExample.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный параметр timeframe"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    )
                    }
    )
    @GetMapping
    public Map<String, Object> getStatistics(
            @RequestParam(required = false, defaultValue = "month")
            @Schema(
                    description = "Выбор временного диапазона",
                    allowableValues = {"week", "month", "year"},
                    example = "month"
            )
            String timeframe) {

        log.info("GET /statistics timeframe={}", timeframe);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User currentUser =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        AppUser appUser = userRepository.findByUsername(currentUser.getUsername());

        Duration period = calculatePeriod(timeframe);

        Map<String, Object> response = new HashMap<>();
        response.put("difficultyStats", appUser.getSolvedProblemsByDifficulty(leetCodeApiService, period));
        response.put("topicStats", appUser.getSolvedProblemsByTopic(leetCodeApiService, period));
        response.put("dayOfWeekStats", appUser.getSolvedProblemsByDayOfWeek());
        response.put("avgTime", appUser.getAverageTimeToSolve(leetCodeApiService, period));
        response.put("firstAttempt", appUser.getFirstAttemptStats(leetCodeApiService, period));

        log.info("Статистика рассчитана для пользователя {}", currentUser.getUsername());

        return response;
    }


    private Duration calculatePeriod(String timeframe) {
        return switch (timeframe) {
            case "week" -> Duration.ofDays(7);
            case "month" -> Duration.ofDays(30);
            case "year" -> Duration.ofDays(365);
            default -> throw new IllegalArgumentException("Unknown timeframe: " + timeframe);
        };
    }
}
