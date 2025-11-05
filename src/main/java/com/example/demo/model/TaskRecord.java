package com.example.demo.model;

import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TaskRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String taskName;

    private LocalDateTime date;
    private Duration duration;

    private Integer tryCounter;

    public TaskRecord() {}

    public TaskRecord(String taskName, LocalDateTime date, Duration duration, Integer tryCounter) {
        this.taskName = taskName;
        this.date = date;
        this.duration = duration;
        this.tryCounter = tryCounter;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Integer getTryCounter() {
        return tryCounter;
    }

    public void setTryCounter(Integer tryCounter) {
        this.tryCounter = tryCounter;
    }
}