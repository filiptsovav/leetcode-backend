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

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Controller
public class StatisticsController {

    @Autowired
    private LeetCodeApiService leetCodeApiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/statistics")
    public String getStatistics(@RequestParam(required = false) String timeframe, Model model) throws JsonProcessingException {
        if (timeframe == null || timeframe.isEmpty()) {
            timeframe = "month"; // Значение по умолчанию
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User currentUser =
                (org.springframework.security.core.userdetails.User)authentication.getPrincipal();
        AppUser appUser = userRepository.findByUsername(currentUser.getUsername());

        Duration period = calculatePeriod(timeframe);

        List<Double> difficultyStats = appUser.getSolvedProblemsByDifficulty(leetCodeApiService, period);
        Map<String, Long> topicStats = appUser.getSolvedProblemsByTopic(leetCodeApiService, period);
        Map<DayOfWeek, Long> dayOfWeekStats = appUser.getSolvedProblemsByDayOfWeek();
        List<Double> avgTime = appUser.getAverageTimeToSolve(leetCodeApiService, period);
        List<Long> firstAttempt = appUser.getFirstAttemptStats(leetCodeApiService, period);

        model.addAttribute("difficultyStats", objectMapper.writeValueAsString(difficultyStats));
        model.addAttribute("topicStats", objectMapper.writeValueAsString(topicStats));
        model.addAttribute("dayOfWeekStats", objectMapper.writeValueAsString(dayOfWeekStats));
        model.addAttribute("avgTime", objectMapper.writeValueAsString(avgTime));
        model.addAttribute("firstAttempt", objectMapper.writeValueAsString(firstAttempt));
        System.out.println(objectMapper.writeValueAsString(difficultyStats));

        return "statistics";
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