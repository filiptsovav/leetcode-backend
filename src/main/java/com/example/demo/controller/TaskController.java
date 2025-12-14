package com.example.demo.controller;

import com.example.demo.dto.TaskSearchCriteria;
import com.example.demo.model.AppUser;
import com.example.demo.model.TaskRecord;
import com.example.demo.model.leetCodeApiService.Question;
import com.example.demo.repository.RecordRepository;
import com.example.demo.repository.TaskRecordSpecification;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LeetCodeApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@Tag(name = "Task Records", description = "API для записи и поиска выполненных задач")
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
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{ \"success\": true }"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректное имя задачи",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{ \"error\": \"Task does not exist on LeetCode\" }"))
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

        // 1. Получаем задачу с LeetCode (один раз!)
        Question question;
        try {
            question = leetCodeApiService.getQuestion(taskName);
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

        // --- ВАЖНО: ПРИВЯЗКА К ПОЛЬЗОВАТЕЛЮ ---
        record.setUser(appUser); // <--- БЕЗ ЭТОГО ПОИСК НЕ НАЙДЕТ ЗАПИСЬ
        // --------------------------------------

        // Сохраняем контент для поиска
        if (question.getContent() != null) {
            // Очищаем от HTML тегов
            String cleanText = question.getContent().replaceAll("\\<.*?\\>", " ");
            record.setContent(cleanText);
        }

        recordRepository.save(record);

        // Дублируем связь в объекте User (опционально, но полезно для TaskSuggestion)
        appUser.addRecord(record);
        userRepository.save(appUser);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @Operation(
            summary = "Расширенный поиск задач",
            description = "Поиск по названию, дате и попыткам с пагинацией и сортировкой"
    )
    @GetMapping("/api/tasks/search")
    public Page<TaskRecord> searchTasks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) Integer minTries,
            @RequestParam(required = false) Integer maxTries,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        TaskSearchCriteria criteria = new TaskSearchCriteria();

        // Получаем имя текущего юзера для фильтрации
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        criteria.setUsername(auth.getName());

        criteria.setQuery(query);

        if (dateFrom != null && !dateFrom.isEmpty()) {
            criteria.setDateFrom(LocalDateTime.parse(dateFrom));
        }
        if (dateTo != null && !dateTo.isEmpty()) {
            criteria.setDateTo(LocalDateTime.parse(dateTo));
        }

        criteria.setMinTries(minTries);
        criteria.setMaxTries(maxTries);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return recordRepository.findAll(TaskRecordSpecification.getSpec(criteria), pageable);
    }
}