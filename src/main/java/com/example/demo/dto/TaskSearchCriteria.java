package com.example.demo.dto;

import java.time.LocalDateTime;

public class TaskSearchCriteria {
    private String query;           // Текст для поиска (по названию)
    private LocalDateTime dateFrom; // Дата начала периода
    private LocalDateTime dateTo;   // Дата конца периода
    private Integer minTries;       // Мин. количество попыток
    private Integer maxTries;       // Макс. количество попыток
    
    // Поля для сортировки
    private String sortBy = "date"; // Поле сортировки (date, taskName, duration)
    private String sortDir = "desc"; // Направление (asc, desc)

    private String username; 
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // Геттеры и сеттеры
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public LocalDateTime getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDateTime dateFrom) { this.dateFrom = dateFrom; }
    public LocalDateTime getDateTo() { return dateTo; }
    public void setDateTo(LocalDateTime dateTo) { this.dateTo = dateTo; }
    public Integer getMinTries() { return minTries; }
    public void setMinTries(Integer minTries) { this.minTries = minTries; }
    public Integer getMaxTries() { return maxTries; }
    public void setMaxTries(Integer maxTries) { this.maxTries = maxTries; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}