package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.model.TaskRecord;
import com.example.demo.repository.RecordRepository;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.User;

import com.example.demo.model.leetCodeApiService.Question;
import com.example.demo.service.LeetCodeApiService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class LeetCodeApiServiceExampleController {
    @Autowired
    private LeetCodeApiService leetCodeApiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

    @GetMapping("/getQuestion/{questionName}")
    public Question sendRequest(@PathVariable String questionName) {
        return leetCodeApiService.getQuestion(questionName); //TODO can throw exception
    }

    @PostMapping("/taskChosen")
    @Transactional
    public String submitTask(
            @RequestParam String taskName,
            @RequestParam String taskTime,
            @RequestParam String completionDate,
            @RequestParam String attemptNumber,
            HttpServletRequest request,
            Model model) {

        try {
            leetCodeApiService.getQuestion(taskName);
        } catch (Exception e) {
            return "redirect:/taskChosen?error=true";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User)authentication.getPrincipal();
        AppUser appUser = userRepository.findByUsername(currentUser.getUsername());
        LocalDateTime date = LocalDate.parse(completionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        Duration duration = Duration.ofMinutes(Long.parseLong(taskTime));
        Integer tryCounter = Integer.parseInt(attemptNumber);
        TaskRecord record = new TaskRecord(taskName, date, duration, tryCounter);
        recordRepository.save(record);
        appUser.addRecord(record);
        userRepository.save(appUser);
        return "redirect:/taskChosen?success=true";
    }

    @GetMapping("/taskSuggestion")
    public String taskSuggestion(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User)authentication.getPrincipal();
        AppUser appUser = userRepository.findByUsername(currentUser.getUsername());

        List<Question> recommendedTasks = appUser.getRecommendedTasks(leetCodeApiService);
        model.addAttribute("recommendedTasks", recommendedTasks);

        return "taskSuggestion";
    }
}
